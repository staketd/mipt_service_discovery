package edu.phystech.servicemesh.exception;

public class WrongParameterException extends RuntimeException {

    public WrongParameterException(String message) {
        super(message);
    }
}
