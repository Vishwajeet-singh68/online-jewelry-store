package com.jewelry.cart.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String CORRELATION_ID_KEY = "traceId";

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartNotFound(CartNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "CART_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartItemNotFound(CartItemNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "CART_ITEM_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(ProductUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleProductUnavailable(ProductUnavailableException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, "PRODUCT_UNAVAILABLE", ex.getMessage(), request);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, "INSUFFICIENT_STOCK", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidCartOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCartOperation(InvalidCartOperationException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_CART_OPERATION", ex.getMessage(), request);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(ServiceUnavailableException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION", "Database constraint violation occurred", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message("Request validation failed")
                .path(request.getRequestURI())
                .traceId(getTraceId())
                .fieldErrors(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "MALFORMED_JSON", "Malformed JSON request", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", "Invalid parameter type: " + ex.getName(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred", request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .traceId(getTraceId())
                .build();
        return new ResponseEntity<>(response, status);
    }

    private String getTraceId() {
        String traceId = MDC.get(CORRELATION_ID_KEY);
        return traceId != null ? traceId : "unknown";
    }
}
