package com.shopbee.orderservice.external.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String slug;
    private String name;
    private boolean active;
    private BigDecimal salePrice;
    private BigDecimal basePrice;
    private Integer stockQuantity;
}
