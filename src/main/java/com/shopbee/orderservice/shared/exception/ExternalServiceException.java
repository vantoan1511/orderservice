package com.shopbee.orderservice.shared.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalServiceException extends RuntimeException {

    private int status;
    private String errorDetails;

    public ExternalServiceException(int status, String message, String errorDetails) {
        super(message);
        this.status = status;
        this.errorDetails = errorDetails;
    }
}
