package edu.phystech.servicemash.exception;

public class ServiceAlreadyExistsException extends RuntimeException {
    public ServiceAlreadyExistsException(String serviceId) {
        super("Service with id = " + serviceId + " already exists");
    }
}
