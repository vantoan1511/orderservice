package com.shopbee.orderservice.shared.enums;

public enum OrderStatus {
    CREATED,
    PENDING,
    ACCEPTED,
    DECLINED,
    AWAITING_PICKUP,
    AWAITING_SHIPPING,
    CANCELED,
    SHIPPED,
    COMPLETED,
    REFUNDED;
}
