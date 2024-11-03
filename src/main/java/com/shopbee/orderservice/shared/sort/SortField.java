package com.shopbee.orderservice.shared.sort;

import lombok.Getter;

@Getter
public enum SortField {
    CREATED_AT("createdAt"),
    STATUS("orderStatus"),
    USERNAME("username"),
    TOTAL_AMOUNT("totalAmount"),
    PAYMENT_METHOD("paymentMethod");

    private final String column;

    SortField(String column) {
        this.column = column;
    }
}
