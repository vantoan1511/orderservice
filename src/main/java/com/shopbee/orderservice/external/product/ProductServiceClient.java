package com.shopbee.orderservice.external.product;

import com.shopbee.orderservice.shared.exception.mapper.ExternalServiceExceptionMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("products")
@RegisterRestClient(configKey = "productservice")
@RegisterClientHeaders
@RegisterProvider(ExternalServiceExceptionMapper.class)
public interface ProductServiceClient {

    @GET
    @Path("{slug}")
    Product getBySlug(@PathParam("slug") String slug);
}
