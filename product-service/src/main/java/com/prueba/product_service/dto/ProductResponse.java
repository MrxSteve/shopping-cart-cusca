package com.prueba.product_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String title;
    private Double price;
    private String description;
    private String category;
    private String image;
    private RatingResponse rating;
}
