package com.gdgoc.babi_order.order.service;

import com.gdgoc.babi_order.menu.entity.Category;
import com.gdgoc.babi_order.menu.entity.Menu;
import com.gdgoc.babi_order.menu.entity.MenuOption;
import com.gdgoc.babi_order.menu.entity.OptionGroupType;
import com.gdgoc.babi_order.menu.entity.SaleStatus;
import com.gdgoc.babi_order.menu.repository.MenuOptionRepository;
import com.gdgoc.babi_order.menu.repository.MenuRepository;
import com.gdgoc.babi_order.order.dto.request.OrderCreateRequest;
import com.gdgoc.babi_order.order.dto.request.OrderItemOptionRequest;
import com.gdgoc.babi_order.order.dto.request.OrderItemRequest;
import com.gdgoc.babi_order.order.dto.response.OrderDetailResponse;
import com.gdgoc.babi_order.order.exception.OrderApiException;
import com.gdgoc.babi_order.order.exception.OrderNotFoundException;
import com.gdgoc.babi_order.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuOptionRepository menuOptionRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, menuOptionRepository);
    }

    @Test
    void createOrderCalculatesAmountFromServerPrices() {
        Menu menu = menu(1L, SaleStatus.AVAILABLE);
        MenuOption option = option(1L, menu, 1000, 2);
        given(orderRepository.findMaxPickupNumber()).willReturn(7);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        given(menuOptionRepository.findById(1L)).willReturn(Optional.of(option));
        given(orderRepository.save(any())).willAnswer(invocation -> {
            Object order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 1L);
            return order;
        });
        OrderCreateRequest request = OrderCreateRequest.builder()
                .items(List.of(OrderItemRequest.builder()
                        .menuId(1L)
                        .quantity(2)
                        .options(List.of(OrderItemOptionRequest.builder()
                                .menuOptionId(1L)
                                .quantity(1)
                                .build()))
                        .build()))
                .build();

        OrderDetailResponse result = orderService.createOrder(request);

        assertThat(result.getPickupNumber()).isEqualTo(8);
        assertThat(result.getTotalAmount()).isEqualTo(18000);
        assertThat(result.getItems().getFirst().getMenuName()).isEqualTo("바비 비빔밥");
        assertThat(result.getItems().getFirst().getOptions().getFirst().getAdditionalPrice())
                .isEqualTo(1000);
    }

    @Test
    void createOrderRejectsOptionFromAnotherMenu() {
        Menu orderedMenu = menu(1L, SaleStatus.AVAILABLE);
        Menu anotherMenu = menu(2L, SaleStatus.AVAILABLE);
        MenuOption option = option(1L, anotherMenu, 1000, 1);
        given(orderRepository.findMaxPickupNumber()).willReturn(0);
        given(menuRepository.findById(1L)).willReturn(Optional.of(orderedMenu));
        given(menuOptionRepository.findById(1L)).willReturn(Optional.of(option));

        assertThatThrownBy(() -> orderService.createOrder(request(1L, 1L, 1)))
                .isInstanceOf(OrderApiException.class)
                .extracting("code")
                .isEqualTo("INVALID_MENU_OPTION");
    }

    @Test
    void createOrderRejectsSoldOutMenu() {
        Menu menu = menu(1L, SaleStatus.SOLDOUT);
        given(orderRepository.findMaxPickupNumber()).willReturn(0);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));

        assertThatThrownBy(() -> orderService.createOrder(OrderCreateRequest.builder()
                        .items(List.of(OrderItemRequest.builder()
                                .menuId(1L)
                                .quantity(1)
                                .options(List.of())
                                .build()))
                        .build()))
                .isInstanceOf(OrderApiException.class)
                .extracting("code")
                .isEqualTo("MENU_SOLD_OUT");
    }

    @Test
    void getOrderThrowsExceptionWhenOrderDoesNotExist() {
        given(orderRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(999L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    private OrderCreateRequest request(Long menuId, Long optionId, Integer optionQuantity) {
        return OrderCreateRequest.builder()
                .items(List.of(OrderItemRequest.builder()
                        .menuId(menuId)
                        .quantity(1)
                        .options(List.of(OrderItemOptionRequest.builder()
                                .menuOptionId(optionId)
                                .quantity(optionQuantity)
                                .build()))
                        .build()))
                .build();
    }

    private Menu menu(Long id, SaleStatus status) {
        Category category = Category.builder().name("밥류").displayOrder(1).build();
        ReflectionTestUtils.setField(category, "id", id);
        Menu menu = Menu.builder()
                .category(category)
                .name("바비 비빔밥")
                .basePrice(8000)
                .displayOrder(1)
                .saleStatus(status)
                .build();
        ReflectionTestUtils.setField(menu, "id", id);
        return menu;
    }

    private MenuOption option(Long id, Menu menu, Integer price, Integer maxQuantity) {
        MenuOption option = MenuOption.builder()
                .menu(menu)
                .groupType(OptionGroupType.SIZE)
                .name("곱빼기")
                .additionalPrice(price)
                .maxQuantity(maxQuantity)
                .displayOrder(1)
                .build();
        ReflectionTestUtils.setField(option, "id", id);
        return option;
    }
}
