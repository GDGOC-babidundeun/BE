package com.gdgoc.babi_order.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "결제 취소 요청")
public class PaymentCancelRequest {

    @Schema(description = "취소 사유", example = "고객 요청")
    private String cancelReason;
}
