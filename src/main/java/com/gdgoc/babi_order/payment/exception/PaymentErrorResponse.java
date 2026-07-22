package com.gdgoc.babi_order.payment.exception;

import java.time.LocalDateTime;

public record PaymentErrorResponse(
        int status,
        String code,
        String message,
        LocalDateTime timestamp
) {
}
