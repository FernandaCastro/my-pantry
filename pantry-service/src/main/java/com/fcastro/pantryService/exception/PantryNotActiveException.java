package com.fcastro.pantryService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PantryNotActiveException extends RuntimeException {
    public PantryNotActiveException(String message) {
        super(message);
    }
}
