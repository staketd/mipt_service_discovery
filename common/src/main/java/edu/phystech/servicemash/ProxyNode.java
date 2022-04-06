package edu.phystech.servicemash;

import java.net.InetSocketAddress;

public class ProxyNode extends Node {
    private BalancingType balancingType;

    public ProxyNode(String address, int port, BalancingType balancingType) {
        super(address, port);
        this.balancingType = balancingType;
    }

    public BalancingType getBalancingType() {
        return balancingType;
    }

    public void setBalancingType(BalancingType balancingType) {
        this.balancingType = balancingType;
    }
}
