package edu.phystech.servicemesh.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;


@Getter
@Setter
public class AddressPortLayout extends IntResourceAllocator {
    public static final int MIN_PORT = 1024;
    public static final int MAX_PORT = (1 << 16) - 1;

    public AddressPortLayout() {
        super(MIN_PORT, MAX_PORT);
    }

    public int allocatePort() {
        return allocate();
    }

    public void deallocatePort(int port) {
        deallocate(port);
    }
}
