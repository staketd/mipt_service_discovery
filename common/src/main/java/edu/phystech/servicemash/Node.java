package edu.phystech.servicemash;

import java.util.Objects;

public class Node {
    private String address;
    private int port;

    public Node() {
    }

    public Node(String address) {
        this.address = address;
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
        return Objects.hash(address, port);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return address.equals(node.address) && port == node.port;
    }
}
