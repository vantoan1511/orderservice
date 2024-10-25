package com.shopbee.orderservice.repository;

import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.shared.page.PageRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    public Optional<Order> findByIdAndUsername(Long id, String username) {
        return find("id = ?1 AND username = ?2", id, username).stream().findFirst();
    }

    public List<Order> findBy(String username, PageRequest pageRequest) {
        return find("username", username).page(pageRequest.getPage() - 1, pageRequest.getSize()).list();
    }

    public long countBy(String username) {
        return count("username", username);
    }
}
