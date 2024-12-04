package com.shopbee.orderservice.external.product;

import com.shopbee.orderservice.external.product.dto.Product;
import com.shopbee.orderservice.external.product.dto.UpdatePartialProductRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class ProductService {

    private final ProductServiceClient productServiceClient;

    @Inject
    public ProductService(@RestClient ProductServiceClient productServiceClient) {
        this.productServiceClient = productServiceClient;
    }

    public void updatePartially(String slug, UpdatePartialProductRequest updatePartialProductRequest) {
        try {
            productServiceClient.updatePartially(slug, updatePartialProductRequest);
        } catch (Exception e) {
            log.warn("Failed to update product partially {}", e.getMessage());
        }
    }

    public Product getBySlug(String productSlug) {
        try {
            return productServiceClient.getBySlug(productSlug);
        } catch (Exception e) {
            log.warn("Failed to get product {} {}", productSlug, e.getMessage());
        }
        return null;
    }
}
