package com.jewelry.inventory.exception;

/**
 * Thrown when a stock operation is requested but insufficient stock exists.
 * Maps to HTTP 409 CONFLICT.
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
