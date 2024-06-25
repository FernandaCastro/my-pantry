package com.fcastro.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class KeyPairException extends RuntimeException {
    public KeyPairException(String message, Throwable cause) {
        super(message, cause);
    }
}

