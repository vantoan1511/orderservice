package com.shopbee.orderservice.shared.enums;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

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
        CANCELED.allowedTransitions = EnumSet.of(REFUNDED);
        SHIPPED.allowedTransitions = EnumSet.of(COMPLETED);
        COMPLETED.allowedTransitions = EnumSet.noneOf(OrderStatus.class);
        REFUNDED.allowedTransitions = EnumSet.noneOf(OrderStatus.class);
    }

    public boolean canTransitionTo(OrderStatus orderStatus) {
        return allowedTransitions.contains(orderStatus);
    }

    public static OrderStatus from(String code) {
        return Stream.of(values())
                .filter(orderStatus -> orderStatus.name().equals(code))
                .findFirst()
                .orElse(null);
    }
}
