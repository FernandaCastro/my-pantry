package com.fcastro.purchaseService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoItemToPurchase extends RuntimeException {

    public NoItemToPurchase(String message) {
        super(message);
    }
}
