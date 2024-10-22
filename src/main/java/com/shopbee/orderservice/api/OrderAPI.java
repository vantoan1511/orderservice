package com.shopbee.orderservice.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderAPI {

    @GET
    public Response getOrders() {
        return Response.ok().build();
    }
}
