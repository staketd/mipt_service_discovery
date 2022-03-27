package edu.phystech.servicemash.exception;

public class ServiceAlreadyExistsException extends RuntimeException {
    public ServiceAlreadyExistsException(long serviceId) {
        super("Service with id = " + serviceId + " already exists");
    }
}
