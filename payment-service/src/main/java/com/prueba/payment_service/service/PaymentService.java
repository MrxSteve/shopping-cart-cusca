package com.prueba.payment_service.service;

import com.prueba.payment_service.client.OrderClient;
import com.prueba.payment_service.dto.CreatePaymentRequest;
import com.prueba.payment_service.dto.PaymentResponse;
import com.prueba.payment_service.entity.Payment;
import com.prueba.payment_service.entity.PaymentStatus;
import com.prueba.payment_service.exception.PaymentNotFoundException;
import com.prueba.payment_service.mapper.PaymentMapper;
import com.prueba.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentResponse processPayment(CreatePaymentRequest request) {
        PaymentStatus status = simulate() ? PaymentStatus.APPROVED : PaymentStatus.REJECTED;

        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setStatus(status);

        Payment saved = paymentRepository.save(payment);

        // Update order status based on payment outcome
        String orderStatus = status == PaymentStatus.APPROVED ? "PAID" : "CANCELLED";
        orderClient.updateOrderStatus(request.getOrderId(), Map.of("status", orderStatus));

        return paymentMapper.toPaymentResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        return paymentMapper.toPaymentResponse(
                paymentRepository.findById(id)
                        .orElseThrow(() -> new PaymentNotFoundException(id))
        );
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        return paymentMapper.toPaymentResponseList(paymentRepository.findByOrderId(orderId));
    }

    // 80% success rate simulation
    private boolean simulate() {
        return ThreadLocalRandom.current().nextInt(100) < 80;
    }
}
