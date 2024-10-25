package com.shopbee.orderservice.service;

import com.shopbee.orderservice.converter.OrderConverter;
import com.shopbee.orderservice.converter.OrderDetailsConverter;
import com.shopbee.orderservice.dto.CreateOrderRequest;
import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.entity.OrderDetails;
import com.shopbee.orderservice.external.product.Product;
import com.shopbee.orderservice.external.product.ProductServiceClient;
import com.shopbee.orderservice.repository.OrderRepository;
import com.shopbee.orderservice.shared.enums.OrderStatus;
import com.shopbee.orderservice.shared.enums.PaymentMethod;
import com.shopbee.orderservice.shared.exception.OrderServiceException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class OrderService {

    private final OrderRepository orderRepository;
    private final SecurityIdentity identity;
    private final OrderConverter orderConverter;
    private final OrderDetailsConverter orderDetailsConverter;
    private final ProductServiceClient productServiceClient;

    @Inject
    public OrderService(OrderRepository orderRepository,
                        SecurityIdentity identity,
                        OrderConverter orderConverter,
                        OrderDetailsConverter orderDetailsConverter,
                        @RestClient ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.identity = identity;
        this.orderConverter = orderConverter;
        this.orderDetailsConverter = orderDetailsConverter;
        this.productServiceClient = productServiceClient;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest createOrderRequest) {
        PaymentMethod paymentMethod = PaymentMethod.from(createOrderRequest.getPaymentMethod());
        if (paymentMethod == null) {
            throw new OrderServiceException("Payment method is not supported", Response.Status.BAD_REQUEST);
        }

        Order order = orderConverter.convert(createOrderRequest);

        List<OrderDetails> orderDetails = orderDetailsConverter.convertAll(createOrderRequest.getItems());
        orderDetails.forEach(orderItem -> {
            Product product = productServiceClient.getBySlug(orderItem.getProductSlug());
            if (orderItem.getQuantity() > product.getStockQuantity()) {
                throw new OrderServiceException("Product " + orderItem.getProductSlug() + " is not enough request quantity", Response.Status.BAD_REQUEST);
            }
            orderItem.setPrice(product.getSalePrice());
            orderItem.setOrder(order);
        });

        BigDecimal totalAmount = orderDetails.stream()
                .map(OrderDetails::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setOrderDetails(orderDetails);
        order.setTotalAmount(totalAmount);
        order.setUsername(identity.getPrincipal().getName());
        orderRepository.persist(order);
        return order;
    }
}
