package com.gdgoc.babi_order.order.dto.response;

import com.gdgoc.babi_order.order.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderDetailResponse {

    private Long id;
    private Integer pickupNumber;
    private String status;
    private Integer totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;

    public static OrderDetailResponse from(Order order) {
        return OrderDetailResponse.builder()
                .id(order.getId())
                .pickupNumber(order.getPickupNumber())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream().map(OrderItemResponse::from).toList())
                .build();
    }
}
