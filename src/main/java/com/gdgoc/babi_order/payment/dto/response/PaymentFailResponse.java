package com.gdgoc.babi_order.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "결제 실패 콜백 응답")
public class PaymentFailResponse {

    @Schema(description = "토스 에러 코드")
    private String code;

    @Schema(description = "실패 메시지")
    private String message;

    @Schema(description = "토스 주문번호")
    private String orderId;
}
