package com.gdgoc.babi_order.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "결제 조회 응답")
public class PaymentResponse {

    @Schema(description = "결제 ID")
    private Long id;

    @Schema(description = "토스 결제 키")
    private String paymentKey;

    @Schema(description = "토스 주문번호")
    private String orderId;

    @Schema(description = "결제 금액")
    private Integer amount;

    @Schema(description = "결제 상태", example = "DONE")
    private String status;

    @Schema(description = "취소 사유")
    private String cancelReason;

    @Schema(description = "결제 승인 시각")
    private LocalDateTime approvedAt;

    @Schema(description = "결제 생성 시각")
    private LocalDateTime createdAt;
}
