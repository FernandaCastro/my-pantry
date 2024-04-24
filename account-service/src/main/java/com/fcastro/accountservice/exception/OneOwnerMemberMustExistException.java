package com.fcastro.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class OneOwnerMemberMustExistException extends RuntimeException {
    public OneOwnerMemberMustExistException(String message) {
        super(message);
    }
}
