package com.gdgoc.babi_order.client;

import com.gdgoc.babi_order.config.TossPaymentProperties;
import com.gdgoc.babi_order.exception.TossPaymentException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final RestTemplate restTemplate;
    private final TossPaymentProperties properties;

    private static final String PAYMENTS_URL = "/v1/payments";

    private HttpHeaders createHeaders() {
        String credentials = properties.getSecretKey() + ":";
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encoded);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public TossPaymentResponse confirm(String paymentKey, String orderId, Integer amount) {
        String url = properties.getBaseUrl() + PAYMENTS_URL + "/confirm";
        Map<String, Object> body = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );

        try {
            ResponseEntity<TossPaymentResponse> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(body, createHeaders()), TossPaymentResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            TossErrorResponse error = e.getResponseBodyAs(TossErrorResponse.class);
            throw new TossPaymentException(error != null ? error.getMessage() : "토스 결제 승인 실패");
        }
    }

    public TossPaymentResponse cancel(String paymentKey, String cancelReason) {
        String url = properties.getBaseUrl() + PAYMENTS_URL + "/" + paymentKey + "/cancel";
        Map<String, Object> body = Map.of("cancelReason", cancelReason);

        try {
            ResponseEntity<TossPaymentResponse> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(body, createHeaders()), TossPaymentResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            TossErrorResponse error = e.getResponseBodyAs(TossErrorResponse.class);
            throw new TossPaymentException(error != null ? error.getMessage() : "토스 결제 취소 실패");
        }
    }

    public TossPaymentResponse getPayment(String paymentKey) {
        String url = properties.getBaseUrl() + PAYMENTS_URL + "/" + paymentKey;

        try {
            ResponseEntity<TossPaymentResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(createHeaders()), TossPaymentResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            TossErrorResponse error = e.getResponseBodyAs(TossErrorResponse.class);
            throw new TossPaymentException(error != null ? error.getMessage() : "토스 결제 조회 실패");
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TossPaymentResponse {
        private String paymentKey;
        private String orderId;
        private String status;
        private String approvedAt;
        private Integer totalAmount;
        private String method;
    }

    @Getter
    @NoArgsConstructor
    private static class TossErrorResponse {
        private String code;
        private String message;
    }
}
