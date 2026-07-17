package com.gdgoc.babi_order.controller;

import com.gdgoc.babi_order.dto.request.PaymentCancelRequest;
import com.gdgoc.babi_order.dto.request.PaymentConfirmRequest;
import com.gdgoc.babi_order.dto.response.PaymentConfirmResponse;
import com.gdgoc.babi_order.dto.response.PaymentResponse;
import com.gdgoc.babi_order.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 API")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 성공 콜백", description = "토스 샌드박스/결제창의 successUrl로 자동 호출됩니다. 승인 후 DB에 저장합니다.")
    @GetMapping("/success")
    public ResponseEntity<PaymentConfirmResponse> success(
            @RequestParam("paymentKey") String paymentKey,
            @RequestParam("orderId") String orderId,
            @RequestParam("amount") Integer amount) {
        PaymentConfirmRequest request = PaymentConfirmRequest.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();
        return ResponseEntity.ok(paymentService.confirm(request));
    }

    @Operation(summary = "결제 승인 (수동)", description = "paymentKey, orderId, amount를 직접 입력해서 승인합니다.")
    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirm(@RequestBody PaymentConfirmRequest request) {
        return ResponseEntity.ok(paymentService.confirm(request));
    }

    @Operation(summary = "결제 취소", description = "승인된 결제를 취소합니다.")
    @PostMapping("/{paymentKey}/cancel")
    public ResponseEntity<PaymentResponse> cancel(
            @PathVariable("paymentKey") String paymentKey,
            @RequestBody PaymentCancelRequest request) {
        return ResponseEntity.ok(paymentService.cancel(paymentKey, request));
    }

    @Operation(summary = "결제 조회 (paymentKey)", description = "토스 결제 키로 결제 내역을 조회합니다.")
    @GetMapping("/{paymentKey}")
    public ResponseEntity<PaymentResponse> getByPaymentKey(@PathVariable("paymentKey") String paymentKey) {
        return ResponseEntity.ok(paymentService.getByPaymentKey(paymentKey));
    }

    @Operation(summary = "결제 조회 (orderId)", description = "토스 주문번호로 결제 내역을 조회합니다.")
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<PaymentResponse> getByOrderId(@PathVariable("orderId") String orderId) {
        return ResponseEntity.ok(paymentService.getByOrderId(orderId));
    }
}
