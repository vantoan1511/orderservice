package com.shopbee.orderservice.converter;

import com.shopbee.orderservice.dto.CreateOrderRequest;
import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.shared.enums.PaymentMethod;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderConverter implements IConverter<CreateOrderRequest, Order> {

    @Override
    public Order convert(CreateOrderRequest createOrderRequest) {
        if (createOrderRequest == null) {
            return null;
        }

        Order order = new Order();
        order.setPaymentMethod(PaymentMethod.from(createOrderRequest.getPaymentMethod()));
        order.setShippingAddress(createOrderRequest.getShippingAddress());
        return order;
    }

    @Override
    public CreateOrderRequest reverse(Order order) {
        return null;
    }
}
