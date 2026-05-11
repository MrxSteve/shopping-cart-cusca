package com.prueba.order_service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerResponse {
    private Long id;
    private String fullName;
    private String email;
}
