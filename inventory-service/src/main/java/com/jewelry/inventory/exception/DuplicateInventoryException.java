package com.jewelry.inventory.exception;

/**
 * Thrown when attempting to create inventory for a product that already has one.
 * Maps to HTTP 409 CONFLICT.
 */
public class DuplicateInventoryException extends RuntimeException {
    public DuplicateInventoryException(String message) {
        super(message);
    }
}
