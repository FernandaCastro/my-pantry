package com.fcastro.purchase.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PurchaseItemsMissingException extends RuntimeException {
    public PurchaseItemsMissingException(String message) {
        super(message);
    }
}
