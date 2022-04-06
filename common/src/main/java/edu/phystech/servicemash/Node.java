package edu.phystech.servicemash;

import java.net.InetSocketAddress;

public class Node {
    private String address;
    private int port;

    public Node() {
    }

    public Node(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }
}
