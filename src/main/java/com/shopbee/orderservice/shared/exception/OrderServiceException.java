package com.shopbee.orderservice.shared.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class OrderServiceException extends WebApplicationException {

    public OrderServiceException(String message, int status) {
        super(message, status);
    }

    public OrderServiceException(String message, Response.Status status) {
        super(message, status);
    }
}
