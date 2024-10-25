package com.shopbee.orderservice.api;

import com.shopbee.orderservice.dto.CreateOrderRequest;
import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.service.OrderService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@Path("orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderAPI {

    private final OrderService orderService;

    @Inject
    public OrderAPI(OrderService orderService) {
        this.orderService = orderService;
    }

    @GET
    @Authenticated
    public Response getOrders() {
        return Response.ok().build();
    }

    @POST
    @Authenticated
    public Response createOrder(@Valid CreateOrderRequest createOrderRequest,
                                @Context UriInfo uriInfo) {
        Order order = orderService.createOrder(createOrderRequest);
        URI uri = uriInfo.getAbsolutePathBuilder().build(order.getId());
        return Response.created(uri).entity(order).build();
    }
}
