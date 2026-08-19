package com.jewelry.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private String getCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader("X-Correlation-ID");
        return correlationId != null ? correlationId : UUID.randomUUID().toString();
    }

    private String getCurrentTimestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(getCurrentTimestamp())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message("Request validation failed")
                .path(request.getRequestURI())
                .traceId(getCorrelationId(request))
                .fieldErrors(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, "USER_ALREADY_EXISTS", request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, "NOT_FOUND", request);
    }

    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationExceptions(
            Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, "FORBIDDEN", request);
    }

    @ExceptionHandler({InvalidTokenException.class, TokenExpiredException.class})
    public ResponseEntity<ErrorResponse> handleTokenExceptions(
            Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(getCurrentTimestamp())
                .status(HttpStatus.CONFLICT.value())
                .error("CONFLICT")
                .message("Database constraint violation")
                .path(request.getRequestURI())
                .traceId(getCorrelationId(request))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({BadRequestException.class, HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(
            Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "BAD_REQUEST", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllOtherExceptions(
            Exception ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(getCurrentTimestamp())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
                .traceId(getCorrelationId(request))
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception ex, HttpStatus status, String error, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(getCurrentTimestamp())
                .status(status.value())
                .error(error)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .traceId(getCorrelationId(request))
                .build();
        return new ResponseEntity<>(response, status);
    }
}
