package com.jewelry.order.exception;

public class OrderCancellationException extends RuntimeException {
    public OrderCancellationException(String message) {
        super(message);
    }
}
