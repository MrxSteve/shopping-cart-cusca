package com.prueba.product_service.dto.external;

import lombok.Data;

@Data
public class FakeStoreProduct {
    private Long id;
    private String title;
    private Double price;
    private String description;
    private String category;
    private String image;
    private FakeStoreRating rating;
}
