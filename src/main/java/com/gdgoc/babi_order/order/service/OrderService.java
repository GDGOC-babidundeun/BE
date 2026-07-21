package com.gdgoc.babi_order.order.service;

import com.gdgoc.babi_order.menu.entity.Menu;
import com.gdgoc.babi_order.menu.entity.MenuOption;
import com.gdgoc.babi_order.menu.entity.SaleStatus;
import com.gdgoc.babi_order.menu.exception.MenuNotFoundException;
import com.gdgoc.babi_order.menu.repository.MenuOptionRepository;
import com.gdgoc.babi_order.menu.repository.MenuRepository;
import com.gdgoc.babi_order.order.dto.request.OrderCreateRequest;
import com.gdgoc.babi_order.order.dto.request.OrderItemOptionRequest;
import com.gdgoc.babi_order.order.dto.request.OrderItemRequest;
import com.gdgoc.babi_order.order.dto.response.OrderDetailResponse;
import com.gdgoc.babi_order.order.dto.response.OrderSummaryResponse;
import com.gdgoc.babi_order.order.entity.Order;
import com.gdgoc.babi_order.order.entity.OrderItem;
import com.gdgoc.babi_order.order.entity.OrderItemOption;
import com.gdgoc.babi_order.order.exception.OrderApiException;
import com.gdgoc.babi_order.order.exception.OrderNotFoundException;
import com.gdgoc.babi_order.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;

    @Transactional
    public OrderDetailResponse createOrder(OrderCreateRequest request) {
        int pickupNumber = orderRepository.findMaxPickupNumber() + 1;
        Order order = new Order(pickupNumber);

        for (OrderItemRequest itemRequest : request.getItems()) {
            order.addItem(createOrderItem(itemRequest));
        }

        return OrderDetailResponse.from(orderRepository.save(order));
    }

    public List<OrderSummaryResponse> getOrders() {
        return orderRepository.findAllByOrderByCreatedAtDescIdDesc().stream()
                .map(OrderSummaryResponse::from)
                .toList();
    }

    public OrderDetailResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return OrderDetailResponse.from(order);
    }

    private OrderItem createOrderItem(OrderItemRequest request) {
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new MenuNotFoundException(request.getMenuId()));
        if (menu.getSaleStatus() == SaleStatus.SOLDOUT) {
            throw new OrderApiException(
                    HttpStatus.CONFLICT,
                    "MENU_SOLD_OUT",
                    "품절된 메뉴는 주문할 수 없습니다. menuId=" + menu.getId()
            );
        }

        OrderItem orderItem = new OrderItem(menu, request.getQuantity());
        List<OrderItemOptionRequest> optionRequests = request.getOptions() == null
                ? List.of() : request.getOptions();
        Set<Long> selectedOptionIds = new HashSet<>();

        for (OrderItemOptionRequest optionRequest : optionRequests) {
            if (!selectedOptionIds.add(optionRequest.getMenuOptionId())) {
                throw new OrderApiException(
                        HttpStatus.BAD_REQUEST,
                        "DUPLICATE_MENU_OPTION",
                        "같은 메뉴 옵션을 중복해서 선택할 수 없습니다. menuOptionId="
                                + optionRequest.getMenuOptionId()
                );
            }
            MenuOption menuOption = menuOptionRepository.findById(optionRequest.getMenuOptionId())
                    .orElseThrow(() -> new OrderApiException(
                            HttpStatus.NOT_FOUND,
                            "MENU_OPTION_NOT_FOUND",
                            "메뉴 옵션을 찾을 수 없습니다. id=" + optionRequest.getMenuOptionId()
                    ));
            validateOption(menu, menuOption, optionRequest.getQuantity());
            orderItem.addOption(new OrderItemOption(menuOption, optionRequest.getQuantity()));
        }
        return orderItem;
    }

    private void validateOption(Menu menu, MenuOption option, Integer quantity) {
        if (!option.getMenu().getId().equals(menu.getId())) {
            throw new OrderApiException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_MENU_OPTION",
                    "해당 메뉴에 속하지 않는 옵션입니다. menuOptionId=" + option.getId()
            );
        }
        if (quantity > option.getMaxQuantity()) {
            throw new OrderApiException(
                    HttpStatus.BAD_REQUEST,
                    "OPTION_QUANTITY_EXCEEDED",
                    "옵션 최대 수량을 초과했습니다. menuOptionId=" + option.getId()
            );
        }
    }
}
