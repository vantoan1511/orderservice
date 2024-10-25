package com.shopbee.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderStatus;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private OffsetDateTime createdAt;
    private OffsetDateTime modifiedAt;
    private String username;
    private List<OrderDetailsResponse> orderDetails;
}
