package com.shopbee.orderservice.api;

import com.shopbee.orderservice.dto.CreateOrderRequest;
import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.service.impl.OrderService;
import com.shopbee.orderservice.shared.page.PageRequest;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@Path("orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderAPI {

    private final OrderService orderService;

    @Inject
    public OrderAPI(OrderService orderService) {
        this.orderService = orderService;
    }

    @GET
    @Authenticated
    public Response getOrders(@Valid PageRequest pageRequest) {
        return Response.ok(orderService.getBy(pageRequest)).build();
    }

    @GET
    @Path("{id}")
    @Authenticated
    public Response getOrders(@PathParam("id") Long id) {
        return Response.ok(orderService.getById(id)).build();
    }

    @PATCH
    @Path("{id}/cancel")
    @Authenticated
    public Response cancelOrder(@PathParam("id") Long id) {
        orderService.cancelOrder(id);
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
