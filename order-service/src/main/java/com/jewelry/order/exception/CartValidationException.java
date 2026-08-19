package com.jewelry.order.exception;

public class CartValidationException extends RuntimeException {
    public CartValidationException(String message) {
        super(message);
    }
}
