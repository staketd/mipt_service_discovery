package edu.phystech.servicemesh.exception;

public class NodeIsNotEmptyException extends RuntimeException {
    public NodeIsNotEmptyException(String nodeId) {
        super(String.format("Node %s cannot be deleted because it has services on it", nodeId));
    }
}
