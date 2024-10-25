package com.shopbee.orderservice.converter;

import com.shopbee.orderservice.dto.OrderResponse;
import com.shopbee.orderservice.entity.Order;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderResponseConverter implements IConverter<Order, OrderResponse> {

    @Override
    public OrderResponse convert(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderStatus(order.getOrderStatus().name());
        response.setPaymentMethod(order.getPaymentMethod().name());
        response.setShippingAddress(order.getShippingAddress());
        response.setTotalAmount(order.getTotalAmount());
        response.setUsername(order.getUsername());
        response.setCreatedAt(order.getCreatedAt());
        response.setModifiedAt(order.getModifiedAt());
        return response;
    }

    @Override
    public Order reverse(OrderResponse orderResponse) {
        return null;
    }
}
