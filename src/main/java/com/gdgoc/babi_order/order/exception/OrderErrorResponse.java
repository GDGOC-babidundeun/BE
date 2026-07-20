package com.gdgoc.babi_order.order.exception;

import java.time.LocalDateTime;

public record OrderErrorResponse(
        int status,
        String code,
        String message,
        LocalDateTime timestamp
) {
}
