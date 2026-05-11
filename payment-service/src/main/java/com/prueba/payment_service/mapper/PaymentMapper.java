package com.prueba.payment_service.mapper;

import com.prueba.payment_service.dto.PaymentResponse;
import com.prueba.payment_service.entity.Payment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toPaymentResponse(Payment payment);
    List<PaymentResponse> toPaymentResponseList(List<Payment> payments);
}
