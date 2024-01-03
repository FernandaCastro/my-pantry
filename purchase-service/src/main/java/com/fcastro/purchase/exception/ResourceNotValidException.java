package com.fcastro.purchase.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class ResourceNotValidException extends RuntimeException {
    public ResourceNotValidException(String message) {
        super(message);
    }
}
