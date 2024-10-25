package com.shopbee.orderservice.service.impl;

import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.service.IOrderStatus;
import com.shopbee.orderservice.shared.enums.OrderStatus;
import com.shopbee.orderservice.shared.exception.OrderServiceException;
import jakarta.ws.rs.core.Response;

public class OrderCancelStatus implements IOrderStatus {

    private final Order order;

    public OrderCancelStatus(Order order) {
        this.order = order;
    }

    @Override
    public void cancel() {
        if (!order.getOrderStatus().canTransitionTo(OrderStatus.CANCELED)) {
            throw new OrderServiceException("Operation not allowed", Response.Status.METHOD_NOT_ALLOWED);
        }

        this.order.setOrderStatus(OrderStatus.CANCELED);
    }
}
