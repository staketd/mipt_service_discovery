package edu.phystech.servicemash;

import java.net.InetSocketAddress;

public class Node {
    private InetSocketAddress address;

    public Node() {
    }

    public Node(InetSocketAddress address) {
        this.address = address;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }
}
