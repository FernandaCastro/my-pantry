package com.fcastro.pantry.config;

public class PantryServiceException extends RuntimeException {
    private int httpStatusCode;

    public PantryServiceException(int httpStatusCode, String message) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }
}
