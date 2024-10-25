package com.shopbee.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsResponse {
    private Long id;
    private Integer quantity;
    private BigDecimal price;
    private String productSlug;
}
