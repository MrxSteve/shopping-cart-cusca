package com.prueba.payment_service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "order-service")
public interface OrderClient {

    @PutMapping("/orders/{id}/status")
    @CircuitBreaker(name = "order-service", fallbackMethod = "updateStatusFallback")
    @Retry(name = "order-service")
    void updateOrderStatus(@PathVariable("id") Long orderId, @RequestBody Map<String, String> body);

    default void updateStatusFallback(Long orderId, Map<String, String> body, Throwable t) {
        throw new RuntimeException(
                "Order service is unavailable. Could not update status for order id: " + orderId
        );
    }
}
