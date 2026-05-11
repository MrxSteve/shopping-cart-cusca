package com.prueba.order_service.client;

import com.prueba.order_service.dto.external.ProductResponse;
import com.prueba.order_service.exception.ProductValidationException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", path = "/products")
public interface ProductClient {

    @GetMapping("/{id}")
    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductFallback")
    ProductResponse getProductById(@PathVariable("id") Long id);

    default ProductResponse getProductFallback(Long id, Throwable t) {
        throw new ProductValidationException(
                "Product service is unavailable. Cannot validate product id: " + id
        );
    }
}
