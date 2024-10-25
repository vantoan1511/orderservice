package com.shopbee.orderservice.shared.filter;

import com.shopbee.orderservice.shared.enums.OrderStatus;
import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterCriteria {

    @QueryParam("keyword")
    private String keyword;

    @QueryParam("status")
    private OrderStatus status;
}