package com.prueba.product_service.client;

import com.prueba.product_service.dto.external.FakeStoreProduct;
import com.prueba.product_service.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FakeStoreClient {

    private final RestClient fakeStoreRestClient;

    @CircuitBreaker(name = "fakestore", fallbackMethod = "fallbackGetAllProducts")
    @Retry(name = "fakestore")
    public List<FakeStoreProduct> getAllProducts(Integer limit) {
        log.debug("Fetching all products from FakeStore (limit={})", limit);
        String uri = limit != null ? "/products?limit={limit}" : "/products";
        return limit != null
            ? fakeStoreRestClient.get()
                .uri(uri, limit)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {})
            : fakeStoreRestClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @CircuitBreaker(name = "fakestore", fallbackMethod = "fallbackGetProductById")
    @Retry(name = "fakestore")
    public FakeStoreProduct getProductById(Long id) {
        log.debug("Fetching product {} from FakeStore", id);
        return fakeStoreRestClient.get()
            .uri("/products/{id}", id)
            .retrieve()
            .body(FakeStoreProduct.class);
    }

    @CircuitBreaker(name = "fakestore", fallbackMethod = "fallbackGetCategories")
    @Retry(name = "fakestore")
    public List<String> getCategories() {
        log.debug("Fetching categories from FakeStore");
        return fakeStoreRestClient.get()
            .uri("/products/categories")
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
    }

    @CircuitBreaker(name = "fakestore", fallbackMethod = "fallbackGetProductsByCategory")
    @Retry(name = "fakestore")
    public List<FakeStoreProduct> getProductsByCategory(String category) {
        log.debug("Fetching products for category '{}' from FakeStore", category);
        return fakeStoreRestClient.get()
            .uri("/products/category/{category}", category)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
    }

    // --- Fallback methods ---

    List<FakeStoreProduct> fallbackGetAllProducts(Integer limit, Exception ex) {
        log.error("Circuit breaker fallback for getAllProducts: {}", ex.getMessage());
        throw new ExternalServiceException("Product catalog is temporarily unavailable. Please try again later.");
    }

    FakeStoreProduct fallbackGetProductById(Long id, Exception ex) {
        log.error("Circuit breaker fallback for getProductById({}): {}", id, ex.getMessage());
        throw new ExternalServiceException("Product service is temporarily unavailable. Please try again later.");
    }

    List<String> fallbackGetCategories(Exception ex) {
        log.error("Circuit breaker fallback for getCategories: {}", ex.getMessage());
        throw new ExternalServiceException("Category service is temporarily unavailable. Please try again later.");
    }

    List<FakeStoreProduct> fallbackGetProductsByCategory(String category, Exception ex) {
        log.error("Circuit breaker fallback for getProductsByCategory({}): {}", category, ex.getMessage());
        throw new ExternalServiceException("Product catalog is temporarily unavailable. Please try again later.");
    }
}
