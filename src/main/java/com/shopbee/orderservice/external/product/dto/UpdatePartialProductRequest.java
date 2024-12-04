package com.shopbee.orderservice.external.product.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePartialProductRequest {

    @Min(value = 0, message = "Quantity must be greater than or equal 0")
    private int stockQuantity;
}
