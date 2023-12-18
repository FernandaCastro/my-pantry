package com.fcastro.pantry.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DatabaseConstraintException extends RuntimeException {
    public DatabaseConstraintException(String message) {
        super(message);
    }
}
