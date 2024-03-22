package com.fcastro.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AtLeastOneMemberMustExistException extends RuntimeException {
    public AtLeastOneMemberMustExistException(String message) {
        super(message);
    }
}
