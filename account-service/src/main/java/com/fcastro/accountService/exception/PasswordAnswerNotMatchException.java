package com.fcastro.accountService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PasswordAnswerNotMatchException extends RuntimeException {
    public PasswordAnswerNotMatchException(String message) {
        super(message);
    }
}
