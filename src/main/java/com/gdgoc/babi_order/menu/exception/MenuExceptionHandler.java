package com.gdgoc.babi_order.menu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class MenuExceptionHandler {

    @ExceptionHandler(MenuNotFoundException.class)
    public ResponseEntity<MenuErrorResponse> handleMenuNotFound(MenuNotFoundException exception) {
        MenuErrorResponse response = new MenuErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "MENU_NOT_FOUND",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
