package com.shopbee.orderservice.service.impl;

import com.shopbee.orderservice.converter.OrderConverter;
import com.shopbee.orderservice.converter.OrderDetailsConverter;
import com.shopbee.orderservice.converter.OrderDetailsResponseConverter;
import com.shopbee.orderservice.converter.OrderResponseConverter;
import com.shopbee.orderservice.dto.CreateOrderRequest;
import com.shopbee.orderservice.dto.OrderDetailsResponse;
import com.shopbee.orderservice.dto.OrderResponse;
import com.shopbee.orderservice.dto.UpdateStatusRequest;
import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.entity.OrderDetails;
import com.shopbee.orderservice.external.product.Product;
import com.shopbee.orderservice.external.product.ProductServiceClient;
import com.shopbee.orderservice.repository.OrderDetailsRepository;
import com.shopbee.orderservice.repository.OrderRepository;
import com.shopbee.orderservice.shared.constants.Role;
import com.shopbee.orderservice.shared.enums.OrderStatus;
import com.shopbee.orderservice.shared.enums.PaymentMethod;
import com.shopbee.orderservice.shared.exception.OrderServiceException;
import com.shopbee.orderservice.shared.filter.FilterCriteria;
import com.shopbee.orderservice.shared.page.PageRequest;
import com.shopbee.orderservice.shared.page.PagedResponse;
import com.shopbee.orderservice.shared.sort.SortCriteria;
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

    private final ProductServiceClient productServiceClient;
    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final SecurityIdentity identity;
    private final OrderConverter orderConverter;
    private final OrderDetailsConverter orderDetailsConverter;
    private final OrderResponseConverter orderResponseConverter;
    private final OrderDetailsResponseConverter orderDetailsResponseConverter;

    @Inject
    public OrderService(OrderRepository orderRepository,
                        SecurityIdentity identity,
                        OrderConverter orderConverter,
                        OrderDetailsConverter orderDetailsConverter,
                        @RestClient ProductServiceClient productServiceClient,
                        OrderResponseConverter orderResponseConverter,
                        OrderDetailsRepository orderDetailsRepository,
                        OrderDetailsResponseConverter orderDetailsResponseConverter) {
        this.orderRepository = orderRepository;
        this.identity = identity;
        this.orderConverter = orderConverter;
        this.orderDetailsConverter = orderDetailsConverter;
        this.productServiceClient = productServiceClient;
        this.orderResponseConverter = orderResponseConverter;
        this.orderDetailsRepository = orderDetailsRepository;
        this.orderDetailsResponseConverter = orderDetailsResponseConverter;
    }

    public PagedResponse<Order> getPagedOrdersByCriteria(FilterCriteria filterCriteria,
                                                         PageRequest pageRequest,
                                                         SortCriteria sortCriteria) {
        List<Order> orders = getByCriteria(filterCriteria, pageRequest, sortCriteria);
        long totalItems = countByCriteria(filterCriteria);
        return PagedResponse.of(totalItems, pageRequest, orders);
    }

    public OrderResponse getOrderResponseById(Long id) {
        Order order = getById(id);
        OrderResponse response = orderResponseConverter.convert(order);
        List<OrderDetails> orderDetails = orderDetailsRepository.findByOrderId(order.getId());
        List<OrderDetailsResponse> orderDetailsResponses = orderDetailsResponseConverter.convertAll(orderDetails);
        response.setOrderDetails(orderDetailsResponses);
        return response;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest createOrderRequest) {
        PaymentMethod paymentMethod = PaymentMethod.from(createOrderRequest.getPaymentMethod());
        if (paymentMethod == null) {
            throw new OrderServiceException("Payment method is not supported", Response.Status.BAD_REQUEST);
        }

        Order order = orderConverter.convert(createOrderRequest);
        List<OrderDetails> orderDetails = createOrderDetails(createOrderRequest, order);
        BigDecimal totalAmount = calculateTotalAmount(orderDetails);

        order.setOrderDetails(orderDetails);
        order.setTotalAmount(totalAmount);
        order.setUsername(identity.getPrincipal().getName());

        if (paymentMethod.equals(PaymentMethod.CASH)) {
            order.setOrderStatus(OrderStatus.PENDING);
        } else {
            order.setOrderStatus(OrderStatus.CREATED);
        }

        orderRepository.persist(order);
        return order;
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = getByIdAndCurrentUser(id);
        if (!order.getOrderStatus().canTransitionTo(OrderStatus.CANCELED)) {
            throw new OrderServiceException("Cannot cancel this order", Response.Status.METHOD_NOT_ALLOWED);
        }

        order.setOrderStatus(OrderStatus.CANCELED);
    }

    @Transactional
    public void completeOrder(Long id) {
        Order order = getByIdAndCurrentUser(id);
        if (!order.getOrderStatus().canTransitionTo(OrderStatus.COMPLETED)) {
            throw new OrderServiceException("Cannot complete this order", Response.Status.METHOD_NOT_ALLOWED);
        }

        order.setOrderStatus(OrderStatus.COMPLETED);
    }

    @Transactional
    public void updateStatus(Long id, UpdateStatusRequest updateStatusRequest) {
        OrderStatus targetStatus = OrderStatus.from(updateStatusRequest.getStatus());
        if (targetStatus == null) {
            throw new OrderServiceException("Unsupported status", Response.Status.BAD_REQUEST);
        }

        Order order = getById(id);
        if (!order.getOrderStatus().canTransitionTo(targetStatus)) {
            throw new OrderServiceException("Cannot change status from " + order.getOrderStatus() + " to " + targetStatus, Response.Status.METHOD_NOT_ALLOWED);
        }

        order.setOrderStatus(targetStatus);
    }

    private List<Order> getByCriteria(FilterCriteria filterCriteria,
                                      PageRequest pageRequest,
                                      SortCriteria sortCriteria) {
        if (!identity.hasRole(Role.ADMIN)) {
            filterCriteria.setUsername(getCurrentUsername());
            return orderRepository.findByCriteria(filterCriteria, pageRequest, sortCriteria);
        }

        return orderRepository.findByCriteria(filterCriteria, pageRequest, sortCriteria);
    }

    private long countByCriteria(FilterCriteria filterCriteria) {
        if (identity.hasRole(Role.ADMIN)) {
            return orderRepository.countBy(null, filterCriteria);
        }

        return orderRepository.countBy(getCurrentUsername(), filterCriteria);
    }

    private BigDecimal calculateTotalAmount(List<OrderDetails> orderDetails) {
        return orderDetails.stream()
                .map(OrderDetails::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderDetails> createOrderDetails(CreateOrderRequest createOrderRequest, Order order) {
        List<OrderDetails> orderDetails = orderDetailsConverter.convertAll(createOrderRequest.getItems());
        orderDetails.forEach(orderItem -> validateAndSetOrder(orderItem, order));
        return orderDetails;
    }

    private void validateAndSetOrder(OrderDetails orderDetails, Order order) {
        Product product = productServiceClient.getBySlug(orderDetails.getProductSlug());
        if (orderDetails.getQuantity() > product.getStockQuantity()) {
            throw new OrderServiceException("Insufficient stock for product: " + orderDetails.getProductSlug(), Response.Status.BAD_REQUEST);
        }

        orderDetails.setPrice(product.getSalePrice());
        orderDetails.setOrder(order);
    }

    private Order getById(Long id) {
        if (identity.hasRole(Role.ADMIN)) {
            return orderRepository.findByIdOptional(id)
                    .orElseThrow(() -> new OrderServiceException("Order not found", Response.Status.NOT_FOUND));
        }

        return getByIdAndCurrentUser(id);
    }

    private Order getByIdAndCurrentUser(Long id) {
        String username = getCurrentUsername();
        return orderRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new OrderServiceException("Order not found", Response.Status.NOT_FOUND));
    }

    private String getCurrentUsername() {
        return identity.getPrincipal().getName();
    }
}
