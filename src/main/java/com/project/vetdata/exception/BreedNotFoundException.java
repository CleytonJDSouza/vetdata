package com.project.vetdata.exception;

public class BreedNotFoundException extends RuntimeException {

    public BreedNotFoundException (String message) {
        super(message);
    }
}
