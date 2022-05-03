package edu.phystech.servicemesh.exception;

public class ServiceAlreadyExistsException extends RuntimeException {
    public ServiceAlreadyExistsException(String serviceId) {
        super("Service with id = " + serviceId + " already exists");
    }
}
