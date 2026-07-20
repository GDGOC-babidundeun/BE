package com.gdgoc.babi_order.order.dto.response;

import com.gdgoc.babi_order.order.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderItemResponse {

    private Long id;
    private Long menuId;
    private String menuName;
    private Integer menuPrice;
    private Integer quantity;
    private Integer lineAmount;
    private List<OrderItemOptionResponse> options;

    public static OrderItemResponse from(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .menuId(item.getMenu().getId())
                .menuName(item.getMenuNameSnapshot())
                .menuPrice(item.getMenuPriceSnapshot())
                .quantity(item.getQuantity())
                .lineAmount(item.getLineAmount())
                .options(item.getOptions().stream().map(OrderItemOptionResponse::from).toList())
                .build();
    }
}
