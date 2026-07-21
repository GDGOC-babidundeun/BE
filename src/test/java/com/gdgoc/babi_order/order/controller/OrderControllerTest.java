package com.gdgoc.babi_order.order.controller;

import com.gdgoc.babi_order.config.SecurityConfig;
import com.gdgoc.babi_order.order.dto.response.OrderDetailResponse;
import com.gdgoc.babi_order.order.dto.response.OrderSummaryResponse;
import com.gdgoc.babi_order.order.exception.OrderExceptionHandler;
import com.gdgoc.babi_order.order.exception.OrderNotFoundException;
import com.gdgoc.babi_order.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({SecurityConfig.class, OrderExceptionHandler.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void createOrderReturnsCreated() throws Exception {
        given(orderService.createOrder(any())).willReturn(OrderDetailResponse.builder()
                .id(1L)
                .pickupNumber(1)
                .status("PREPARING")
                .totalAmount(18000)
                .items(List.of())
                .build());

        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content("""
                                {
                                  "items": [
                                    {
                                      "menuId": 1,
                                      "quantity": 2,
                                      "options": [
                                        {"menuOptionId": 1, "quantity": 1}
                                      ]
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/orders/1"))
                .andExpect(jsonPath("$.status").value("PREPARING"))
                .andExpect(jsonPath("$.totalAmount").value(18000));
    }

    @Test
    void createOrderReturnsBadRequestForEmptyItems() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content("{\"items\": []}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void getOrdersReturnsOrderList() throws Exception {
        given(orderService.getOrders()).willReturn(List.of(OrderSummaryResponse.builder()
                .id(1L)
                .pickupNumber(1)
                .status("PREPARING")
                .totalAmount(18000)
                .build()));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].pickupNumber").value(1));
    }

    @Test
    void getOrderReturnsNotFound() throws Exception {
        given(orderService.getOrder(999L)).willThrow(new OrderNotFoundException(999L));

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ORDER_NOT_FOUND"));
    }
}
