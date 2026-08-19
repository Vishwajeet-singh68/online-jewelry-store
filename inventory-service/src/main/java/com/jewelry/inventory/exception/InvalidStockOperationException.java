package com.jewelry.inventory.exception;

/**
 * Thrown when a stock operation would violate a business rule.
 * E.g., releasing more than reserved, deducting from inactive inventory.
 * Maps to HTTP 409 CONFLICT.
 */
public class InvalidStockOperationException extends RuntimeException {
    public InvalidStockOperationException(String message) {
        super(message);
    }
}
