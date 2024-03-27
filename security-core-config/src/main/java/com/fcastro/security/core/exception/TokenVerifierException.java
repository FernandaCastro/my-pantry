package com.fcastro.security.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class TokenVerifierException extends RuntimeException {
    public TokenVerifierException(String message) {
        super(message);
    }
}
