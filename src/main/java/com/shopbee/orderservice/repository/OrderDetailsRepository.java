package com.shopbee.orderservice.repository;

import com.shopbee.orderservice.entity.OrderDetails;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderDetailsRepository implements PanacheRepository<OrderDetails> {
}
