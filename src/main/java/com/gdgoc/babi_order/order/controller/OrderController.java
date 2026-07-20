package com.gdgoc.babi_order.order.controller;

import com.gdgoc.babi_order.order.dto.request.OrderCreateRequest;
import com.gdgoc.babi_order.order.dto.response.OrderDetailResponse;
import com.gdgoc.babi_order.order.dto.response.OrderSummaryResponse;
import com.gdgoc.babi_order.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문 API")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "주문 생성", description = "메뉴와 옵션의 현재 가격을 기준으로 주문을 생성합니다.")
    public ResponseEntity<OrderDetailResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest request) {
        OrderDetailResponse response = orderService.createOrder(request);
        return ResponseEntity.created(URI.create("/api/orders/" + response.getId())).body(response);
    }

    @GetMapping
    @Operation(summary = "전체 주문 조회", description = "최근 생성된 주문부터 조회합니다.")
    public ResponseEntity<List<OrderSummaryResponse>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "주문 상세 조회", description = "주문 상품과 옵션 스냅샷을 함께 조회합니다.")
    public ResponseEntity<OrderDetailResponse> getOrder(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }
}
