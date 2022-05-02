package edu.phystech.servicemesh.model;

import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class IntResourceAllocator {
    private int minAvailable;
    private int maxAvailable;

    private LinkedList<PortBlock> resourceBlocks;

    public IntResourceAllocator(int minAvailable, int maxAvailable) {
        this.minAvailable = minAvailable;
        this.maxAvailable = maxAvailable;
        resourceBlocks = new LinkedList<>();
        resourceBlocks.add(new PortBlock(minAvailable, maxAvailable));
    }

    protected boolean canAllocate() {
        return !resourceBlocks.isEmpty();
    }

    protected int allocate() {
        PortBlock block = resourceBlocks.getFirst();

        int allocatedResource = block.start;
        if (block.start == block.end) {
            resourceBlocks.removeFirst();
        } else {
            block.start++;
        }

        return allocatedResource;
    }

    protected void deallocate(int value) {
        if (resourceBlocks.isEmpty()) {
            resourceBlocks.addFirst(new PortBlock(value, value));
            return;
        }
        int ind = 0;
        for (PortBlock block: resourceBlocks) {
            if (block.start > value) {
                resourceBlocks.add(ind, new PortBlock(value, value));
                break;
            }
            ++ind;
        }
        PortBlock previousBlock = null;
        for (PortBlock block: resourceBlocks) {
            if (previousBlock == null) {
                previousBlock = block;
                continue;
            }

            if (previousBlock.end + 1 == block.start) {
                previousBlock.end = block.end;
                block.start = block.end = 0;
            } else {
                previousBlock = block;
            }
        }
        resourceBlocks.removeIf(block -> block.start == 0 && block.end == 0);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return resourceBlocks.size() == 1 &&
                resourceBlocks.get(0).start == minAvailable &&
                resourceBlocks.get(0).end == maxAvailable;
    }

    public int getMinAvailable() {
        return minAvailable;
    }

    public int getMaxAvailable() {
        return maxAvailable;
    }

    public void setMinAvailable(int minAvailable) {
        this.minAvailable = minAvailable;
    }

    public void setMaxAvailable(int maxAvailable) {
        this.maxAvailable = maxAvailable;
    }

    public void setResourceBlocks(LinkedList<PortBlock> resourceBlocks) {
        this.resourceBlocks = resourceBlocks;
    }

    public LinkedList<PortBlock> getResourceBlocks() {
        return resourceBlocks;
    }

    private static class PortBlock {
        public int start;
        public int end;

        public PortBlock(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
