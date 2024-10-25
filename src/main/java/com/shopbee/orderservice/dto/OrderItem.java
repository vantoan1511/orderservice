package com.shopbee.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @NotBlank(message = "ProductSlug must not be blank")
    private String productSlug;

    @Min(value = 0, message = "Quantity must be greater than or equal 0")
    private Integer quantity;

}
