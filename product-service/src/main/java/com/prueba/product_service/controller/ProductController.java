package com.prueba.product_service.controller;

import com.prueba.product_service.dto.ProductResponse;
import com.prueba.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) Integer limit) {
        log.info("GET /products (limit={})", limit);
        return ResponseEntity.ok(productService.getAllProducts(limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("GET /products/{}", id);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        log.info("GET /products/categories");
        return ResponseEntity.ok(productService.getCategories());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable String category) {
        log.info("GET /products/category/{}", category);
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }
}
