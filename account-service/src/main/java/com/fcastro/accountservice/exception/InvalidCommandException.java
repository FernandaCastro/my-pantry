package com.fcastro.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidCommandException extends RuntimeException {
    public InvalidCommandException(String message) {
        super(message);
    }
}
