package com.jewelry.order.exception;

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

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderStatus(InvalidOrderStatusException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_ORDER_STATUS", ex.getMessage(), request);
    }

    @ExceptionHandler(OrderCancellationException.class)
    public ResponseEntity<ErrorResponse> handleOrderCancellation(OrderCancellationException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, "ORDER_CANCELLATION_FAILED", ex.getMessage(), request);
    }

    @ExceptionHandler(CartValidationException.class)
    public ResponseEntity<ErrorResponse> handleCartValidation(CartValidationException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "CART_VALIDATION_FAILED", ex.getMessage(), request);
    }

    @ExceptionHandler(InventoryReservationException.class)
    public ResponseEntity<ErrorResponse> handleInventoryReservation(InventoryReservationException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, "INVENTORY_RESERVATION_FAILED", ex.getMessage(), request);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(ServiceUnavailableException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", ex.getMessage(), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);
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
