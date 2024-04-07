package com.fcastro.pantryservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PantryAndProductAccountGroupInconsistentException extends RuntimeException {
    public PantryAndProductAccountGroupInconsistentException(String message) {
        super(message);
    }
}
