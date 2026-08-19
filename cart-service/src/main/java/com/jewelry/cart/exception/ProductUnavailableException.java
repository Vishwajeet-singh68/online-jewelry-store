package com.jewelry.cart.exception;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException(String message) {
        super(message);
    }
}
