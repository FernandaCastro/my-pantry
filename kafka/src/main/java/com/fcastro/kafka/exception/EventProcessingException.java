package com.fcastro.kafka.exception;

import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Getter
public class EventProcessingException extends RuntimeException {
    Map<Serializable, Throwable> throwableMap;

    public EventProcessingException(String message) {
        super(message);
    }

    public EventProcessingException(String message, Throwable ex) {
        super(message);
        addSuppressed(ex);
    }

    public EventProcessingException(String message, Map<Serializable, Throwable> exceptions) {
        super(message);
        throwableMap = exceptions;
    }

}
