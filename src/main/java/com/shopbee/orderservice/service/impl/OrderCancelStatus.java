package com.shopbee.orderservice.service.impl;

import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.service.AbstractOrderStatus;
import com.shopbee.orderservice.shared.exception.OrderServiceException;
import jakarta.ws.rs.core.Response;

public class OrderCancelStatus extends AbstractOrderStatus {

    public OrderCancelStatus(Order order) {
        super(order);
    }

    @Override
    public void cancel() {
        if (!order.getOrderStatus().canTransitionTo(com.shopbee.orderservice.shared.enums.OrderStatus.CANCELED)) {
            throw new OrderServiceException("Operation not allowed", Response.Status.METHOD_NOT_ALLOWED);
        }

        this.order.setOrderStatus(com.shopbee.orderservice.shared.enums.OrderStatus.CANCELED);
    }
}
