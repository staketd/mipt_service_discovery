package edu.phystech.servicemesh.exception;

import edu.phystech.servicemesh.model.ServiceInstanceId;

public class ServiceIsNotPresentOnNodeException extends RuntimeException {
    public ServiceIsNotPresentOnNodeException(ServiceInstanceId instanceId) {
        super(String.format("Service instance %s is not present on node %s", instanceId.getId(), instanceId.getNodeId()));
    }
}
