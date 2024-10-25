package com.shopbee.orderservice.converter;

import com.shopbee.orderservice.dto.OrderItem;
import com.shopbee.orderservice.entity.OrderDetails;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderDetailsConverter implements IConverter<OrderItem, OrderDetails> {

    @Override
    public OrderDetails convert(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setProductSlug(orderItem.getProductSlug());
        orderDetails.setQuantity(orderItem.getQuantity());
        return orderDetails;
    }

    @Override
    public OrderItem reverse(OrderDetails orderDetails) {
        return null;
    }
}
