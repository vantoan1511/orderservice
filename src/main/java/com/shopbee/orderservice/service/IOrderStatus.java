package com.shopbee.orderservice.service;

import com.shopbee.orderservice.shared.exception.OrderServiceException;
import jakarta.ws.rs.core.Response;

public interface IOrderStatus {

    default void cancel() {
        throw new OrderServiceException("Operation not allowed", Response.Status.METHOD_NOT_ALLOWED);
    }
}
