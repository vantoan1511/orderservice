package com.shopbee.orderservice.repository;

import com.shopbee.orderservice.entity.OrderDetails;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class OrderDetailsRepository implements PanacheRepository<OrderDetails> {

    public List<OrderDetails> findByOrderId(Long id) {
        return find("order.id", id).list();
    }
}
