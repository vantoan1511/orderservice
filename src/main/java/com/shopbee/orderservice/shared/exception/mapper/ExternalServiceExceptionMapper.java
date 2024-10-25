package com.shopbee.orderservice.shared.exception.mapper;

import com.shopbee.orderservice.shared.exception.ExternalServiceException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class ExternalServiceExceptionMapper implements ResponseExceptionMapper<ExternalServiceException> {

    @Override
    public ExternalServiceException toThrowable(Response response) {
        if (isSuccessfulStatus(response)) {
            return null;
        }

        String errorDetails = response.readEntity(String.class);
        int status = response.getStatus();
        return new ExternalServiceException(status, response.getStatusInfo().getReasonPhrase(), errorDetails);
    }

    private boolean isSuccessfulStatus(Response response) {
        return response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL);
    }
}
