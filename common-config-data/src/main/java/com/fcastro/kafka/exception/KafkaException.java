package com.fcastro.kafka.exception;

public class KafkaException extends RuntimeException {
    public KafkaException(String message, Throwable ex) {
        super(message);
        addSuppressed(ex);
    }
}
