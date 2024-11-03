package com.shopbee.orderservice.api;

import com.shopbee.orderservice.dto.CreateOrderRequest;
import com.shopbee.orderservice.dto.UpdateStatusRequest;
import com.shopbee.orderservice.entity.Order;
import com.shopbee.orderservice.service.impl.OrderService;
import com.shopbee.orderservice.shared.constants.Role;
import com.shopbee.orderservice.shared.filter.FilterCriteria;
import com.shopbee.orderservice.shared.page.PageRequest;
import com.shopbee.orderservice.shared.sort.SortCriteria;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
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
    public Response getOrders(@BeanParam @Valid FilterCriteria filterCriteria,
                              @BeanParam @Valid PageRequest pageRequest,
                              @BeanParam @Valid SortCriteria sortCriteria) {
        return Response.ok(orderService.getPagedOrdersByCriteria(filterCriteria, pageRequest, sortCriteria)).build();
    }

    @GET
    @Path("{id}")
    @Authenticated
    public Response getById(@PathParam("id") Long id) {
        return Response.ok(orderService.getOrderResponseById(id)).build();
    }

    @PATCH
    @Path("{id}")
    @RolesAllowed({Role.ADMIN})
    public Response updateStatus(@PathParam("id") Long id, @Valid UpdateStatusRequest updateStatusRequest) {
        orderService.updateStatus(id, updateStatusRequest);
        return Response.ok().build();
    }

    @PATCH
    @Path("{id}/cancel")
    @Authenticated
    public Response cancelOrder(@PathParam("id") Long id) {
        orderService.cancelOrder(id);
        return Response.ok().build();
    }

    @PATCH
    @Path("{id}/complete")
    @Authenticated
    public Response completeOrder(@PathParam("id") Long id) {
        orderService.completeOrder(id);
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
