package com.gdgoc.babi_order.order.dto.response;

import com.gdgoc.babi_order.order.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "주문 요약 정보")
public class OrderSummaryResponse {

    private Long id;
    private Integer pickupNumber;
    private String status;
    private Integer totalAmount;
    private LocalDateTime createdAt;

    public static OrderSummaryResponse from(Order order) {
        return OrderSummaryResponse.builder()
                .id(order.getId())
                .pickupNumber(order.getPickupNumber())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
