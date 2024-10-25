package com.shopbee.orderservice.service.impl;

import com.shopbee.orderservice.repository.OrderDetailsRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderDetailsService {

    private final OrderDetailsRepository orderDetailsRepository;

    @Inject
    public OrderDetailsService(OrderDetailsRepository orderDetailsRepository) {
        this.orderDetailsRepository = orderDetailsRepository;
    }
}
