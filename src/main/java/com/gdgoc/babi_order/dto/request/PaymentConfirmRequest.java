package com.gdgoc.babi_order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "결제 승인 요청")
public class PaymentConfirmRequest {

    @Schema(description = "토스 결제 키", example = "tviva20240101...")
    private String paymentKey;

    @Schema(description = "토스 주문번호", example = "MC45NTQ4MTg2NjE2NDM1")
    private String orderId;

    @Schema(description = "결제 금액", example = "15000")
    private Integer amount;
}
