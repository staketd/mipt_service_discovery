package edu.phystech.servicemesh.exception;

import edu.phystech.servicemesh.ServiceInstanceId;

public class ServiceInstanceNotExistsException extends RuntimeException {
    public ServiceInstanceNotExistsException(String serviceId, ServiceInstanceId serviceInstanceId) {
        super(
                String.format(
                        "Service instance id: %s node: %s not exists in service %s",
                        serviceInstanceId.getId(),
                        serviceInstanceId.getNodeId(),
                        serviceId
                )
        );
    }
}
