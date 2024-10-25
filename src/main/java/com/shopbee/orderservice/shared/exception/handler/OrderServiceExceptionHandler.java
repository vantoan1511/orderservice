package com.shopbee.orderservice.shared.exception.handler;

import com.shopbee.orderservice.shared.exception.ErrorResponse;
import com.shopbee.orderservice.shared.exception.OrderServiceException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class OrderServiceExceptionHandler implements ExceptionMapper<OrderServiceException> {

    @Override
    public Response toResponse(OrderServiceException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getMessage());
        return Response.status(e.getResponse().getStatus()).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
    }
}
