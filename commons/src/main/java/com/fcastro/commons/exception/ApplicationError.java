package com.fcastro.commons.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ApplicationError {

    private Long timestamp;
    private int status;
    private String errorType;
    private String errorMessage;
    private String path;

}
