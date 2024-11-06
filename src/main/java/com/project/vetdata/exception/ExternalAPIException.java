package com.project.vetdata.exception;

public class ExternalAPIException extends RuntimeException {
    public ExternalAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}
