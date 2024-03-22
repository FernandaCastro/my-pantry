package com.fcastro.purchaseservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PurchaseAlreadyProcessedException extends RuntimeException {
    public PurchaseAlreadyProcessedException(String message) {
        super(message);
    }
}
