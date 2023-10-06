package com.fcastro.pantryInventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class QuantityNotAvailableException extends RuntimeException {
    public QuantityNotAvailableException(String message) {
        super(message);
    }
}
