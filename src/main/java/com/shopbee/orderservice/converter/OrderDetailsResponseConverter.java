package com.shopbee.orderservice.converter;

import com.shopbee.orderservice.dto.OrderDetailsResponse;
import com.shopbee.orderservice.entity.OrderDetails;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderDetailsResponseConverter implements IConverter<OrderDetails, OrderDetailsResponse> {

    @Override
    public OrderDetailsResponse convert(OrderDetails orderDetails) {
        if (orderDetails == null) {
            return null;
        }

        OrderDetailsResponse response = new OrderDetailsResponse();
        response.setId(orderDetails.getId());
        response.setPrice(orderDetails.getPrice());
        response.setProductSlug(orderDetails.getProductSlug());
        response.setQuantity(orderDetails.getQuantity());
        return response;
    }

    @Override
    public OrderDetails reverse(OrderDetailsResponse orderDetailsResponse) {
        return null;
    }
}
