package com.fcastro.security.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class TokenVerifierException extends RuntimeException {
    public TokenVerifierException(String message) {
        super(message);
    }
}
