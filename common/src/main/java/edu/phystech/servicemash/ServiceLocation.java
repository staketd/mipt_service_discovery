package edu.phystech.servicemash;

import java.util.List;

public class ServiceLocation {
    private String name;
    private ProxyNode proxyNode;
    private List<Node> instances;

    public ServiceLocation(String name, ProxyNode proxyNode, List<Node> instances) {
        this.name = name;
        this.proxyNode = proxyNode;
        this.instances = instances;
    }
}
