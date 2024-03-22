package com.fcastro.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AccountGroupNotDefinedException extends RuntimeException {
    public AccountGroupNotDefinedException(String message) {
        super(message);
    }
}
