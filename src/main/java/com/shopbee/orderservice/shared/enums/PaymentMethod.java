package com.shopbee.orderservice.shared.enums;

import java.util.stream.Stream;

public enum PaymentMethod {
    CASH,
    BANKING;

    public static PaymentMethod from(String code) {
        return Stream.of(values()).filter(each -> each.name().equals(code)).findFirst().orElse(null);
    }
}
