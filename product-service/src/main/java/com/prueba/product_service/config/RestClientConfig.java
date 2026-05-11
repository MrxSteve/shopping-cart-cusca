package com.prueba.product_service.config;

import com.prueba.product_service.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient fakeStoreRestClient(@Value("${fakestore.base-url}") String baseUrl) {
        return RestClient.builder()
            .baseUrl(baseUrl)
            .requestInitializer(request ->
                request.getHeaders().set("Accept", "application/json"))
            .defaultStatusHandler(
                status -> status.is4xxClientError(),
                (request, response) -> {
                    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                        throw new ProductNotFoundException("Resource not found on external service");
                    }
                    throw new RuntimeException("Client error from FakeStore: " + response.getStatusCode());
                }
            )
            .defaultStatusHandler(
                status -> status.is5xxServerError(),
                (request, response) -> {
                    throw new RuntimeException("Server error from FakeStore: " + response.getStatusCode());
                }
            )
            .build();
    }
}
