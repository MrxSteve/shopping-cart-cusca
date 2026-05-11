package com.prueba.product_service.service;

import com.prueba.product_service.client.FakeStoreClient;
import com.prueba.product_service.dto.ProductResponse;
import com.prueba.product_service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final FakeStoreClient fakeStoreClient;
    private final ProductMapper productMapper;

    @Cacheable(value = "products", key = "#limit != null ? #limit : 'all'")
    public List<ProductResponse> getAllProducts(Integer limit) {
        log.info("Fetching all products (limit={})", limit);
        return productMapper.toProductResponseList(fakeStoreClient.getAllProducts(limit));
    }

    @Cacheable(value = "product", key = "#id")
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product by id: {}", id);
        return productMapper.toProductResponse(fakeStoreClient.getProductById(id));
    }

    @Cacheable("categories")
    public List<String> getCategories() {
        log.info("Fetching product categories");
        return fakeStoreClient.getCategories();
    }

    @Cacheable(value = "productsByCategory", key = "#category")
    public List<ProductResponse> getProductsByCategory(String category) {
        log.info("Fetching products by category: {}", category);
        return productMapper.toProductResponseList(fakeStoreClient.getProductsByCategory(category));
    }
}
