package com.fcastro.pantry.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RequestParamExpectedException extends RuntimeException {
    public RequestParamExpectedException(String message) {
        super(message);
    }
}
