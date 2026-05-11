package com.prueba.payment_service.dto;

import com.prueba.payment_service.entity.PaymentMethod;
import com.prueba.payment_service.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}
