package edu.phystech.servicemesh.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Block;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@Getter
@Setter
public class AddressLayout {
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = (1 << 16) - 1;
    private LinkedList<PortBlock> portBlocks;

    public AddressLayout() {
        portBlocks = new LinkedList<>();
        portBlocks.add(new PortBlock(MIN_PORT, MAX_PORT));
    }

    public boolean canAllocate() {
        return !portBlocks.isEmpty();
    }

    public int allocatePort() {
        PortBlock block = portBlocks.getFirst();

        int allocatedPort = block.startPort;
        if (block.startPort == block.endPort) {
            portBlocks.removeFirst();
        } else {
            block.startPort++;
        }

        return allocatedPort;
    }

    public void deallocatePort(int port) {
        if (portBlocks.isEmpty()) {
            portBlocks.addFirst(new PortBlock(port, port));
            return;
        }
        int ind = 0;
        for (PortBlock block: portBlocks) {
            if (block.startPort > port) {
                portBlocks.add(ind, new PortBlock(port, port));
                break;
            }
            ++ind;
        }
        PortBlock previousBlock = null;
        for (PortBlock block: portBlocks) {
            if (previousBlock == null) {
                continue;
            }

            if (previousBlock.endPort + 1 == block.startPort) {
                previousBlock.endPort = block.endPort;
                block.startPort = block.endPort = 0;
            } else {
                previousBlock = block;
            }
        }
        portBlocks.removeIf(block -> block.startPort == 0 && block.endPort == 0);
    }

    private static class PortBlock {
        public int startPort;
        public int endPort;

        public PortBlock(int startPort, int endPort) {
            this.startPort = startPort;
            this.endPort = endPort;
        }
    }
}
