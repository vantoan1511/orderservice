package com.shopbee.orderservice.shared.sort;

import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SortCriteria {

    @QueryParam("sortBy")
    private SortField sortBy = SortField.CREATED_AT;

    @QueryParam("ascending")
    private boolean ascending;
}
