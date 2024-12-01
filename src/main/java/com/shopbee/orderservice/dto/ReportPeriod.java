package com.shopbee.orderservice.dto;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum ReportPeriod {
    DAILY("daily"), WEEKLY("weekly"), MONTHLY("monthly"), QUARTERLY("quarterly");

    private final String code;

    ReportPeriod(String code) {
        this.code = code;
    }

    public static ReportPeriod fromCode(String code) {
        return Stream.of(values()).filter(value -> value.code.equals(code)).findFirst().orElse(null);
    }
}
