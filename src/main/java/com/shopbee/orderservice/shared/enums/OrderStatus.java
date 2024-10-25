package com.shopbee.orderservice.shared.enums;

import java.util.EnumSet;
import java.util.Set;

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

    private Set<OrderStatus> allowedTransitions;

    static {
        CREATED.allowedTransitions = EnumSet.of(PENDING, CANCELED);
        PENDING.allowedTransitions = EnumSet.of(ACCEPTED, DECLINED, CANCELED);
        ACCEPTED.allowedTransitions = EnumSet.of(AWAITING_PICKUP, CANCELED);
        DECLINED.allowedTransitions = EnumSet.of(REFUNDED);
        AWAITING_PICKUP.allowedTransitions = EnumSet.of(AWAITING_SHIPPING, CANCELED);
        AWAITING_SHIPPING.allowedTransitions = EnumSet.of(CANCELED, SHIPPED);
        CANCELED.allowedTransitions = EnumSet.noneOf(OrderStatus.class);
        SHIPPED.allowedTransitions = EnumSet.of(COMPLETED);
        COMPLETED.allowedTransitions = EnumSet.noneOf(OrderStatus.class);
    }

    public boolean canTransitionTo(OrderStatus orderStatus) {
        return allowedTransitions.contains(orderStatus);
    }
}
