package com.shopbee.orderservice.shared.exception.handler;

import com.shopbee.orderservice.shared.exception.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.hibernate.exception.ConstraintViolationException;

@Provider
public class ConstraintViolationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getErrorMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
    }
}
