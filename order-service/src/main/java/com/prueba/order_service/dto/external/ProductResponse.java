package com.prueba.order_service.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String title;
    private BigDecimal price;
    private String category;
    private String description;
    private String image;
}
