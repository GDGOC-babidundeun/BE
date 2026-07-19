package com.gdgoc.babi_order.payment.service;

import com.gdgoc.babi_order.payment.client.TossPaymentClient;
import com.gdgoc.babi_order.payment.entity.Payment;
import com.gdgoc.babi_order.payment.entity.PaymentStatus;
import com.gdgoc.babi_order.payment.dto.request.PaymentCancelRequest;
import com.gdgoc.babi_order.payment.dto.request.PaymentConfirmRequest;
import com.gdgoc.babi_order.payment.dto.response.PaymentConfirmResponse;
import com.gdgoc.babi_order.payment.dto.response.PaymentResponse;
import com.gdgoc.babi_order.payment.exception.TossPaymentException;
import com.gdgoc.babi_order.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentClient tossPaymentClient;

    @Transactional
    public PaymentConfirmResponse confirm(PaymentConfirmRequest request) {
        TossPaymentClient.TossPaymentResponse tossResponse = tossPaymentClient.confirm(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount()
        );

        LocalDateTime approvedAt = OffsetDateTime.parse(tossResponse.getApprovedAt()).toLocalDateTime();
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .paymentKey(request.getPaymentKey())
                .amount(request.getAmount())
                .status(PaymentStatus.DONE)
                .approvedAt(approvedAt)
                .build();

        Payment saved = paymentRepository.save(payment);

        return PaymentConfirmResponse.builder()
                .id(saved.getId())
                .paymentKey(saved.getPaymentKey())
                .orderId(saved.getOrderId())
                .amount(saved.getAmount())
                .status(saved.getStatus().name())
                .approvedAt(saved.getApprovedAt())
                .build();
    }

    @Transactional
    public PaymentResponse cancel(String paymentKey, PaymentCancelRequest request) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new TossPaymentException("결제 정보를 찾을 수 없습니다."));

        tossPaymentClient.cancel(paymentKey, request.getCancelReason());
        payment.cancel(request.getCancelReason());

        return toPaymentResponse(payment);
    }

    public PaymentResponse getByPaymentKey(String paymentKey) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new TossPaymentException("결제 정보를 찾을 수 없습니다."));
        return toPaymentResponse(payment);
    }

    public PaymentResponse getByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new TossPaymentException("해당 주문의 결제 정보를 찾을 수 없습니다."));
        return toPaymentResponse(payment);
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentKey(payment.getPaymentKey())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .cancelReason(payment.getCancelReason())
                .approvedAt(payment.getApprovedAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
