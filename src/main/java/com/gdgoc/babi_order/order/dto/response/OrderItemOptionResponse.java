package com.gdgoc.babi_order.order.dto.response;

import com.gdgoc.babi_order.order.entity.OrderItemOption;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemOptionResponse {

    private Long id;
    private Long menuOptionId;
    private String groupType;
    private String name;
    private Integer additionalPrice;
    private Integer quantity;

    public static OrderItemOptionResponse from(OrderItemOption option) {
        return OrderItemOptionResponse.builder()
                .id(option.getId())
                .menuOptionId(option.getMenuOption() == null ? null : option.getMenuOption().getId())
                .groupType(option.getOptionGroupSnapshot() == null
                        ? null : option.getOptionGroupSnapshot().name())
                .name(option.getOptionNameSnapshot())
                .additionalPrice(option.getAdditionalPriceSnapshot())
                .quantity(option.getQuantity())
                .build();
    }
}
