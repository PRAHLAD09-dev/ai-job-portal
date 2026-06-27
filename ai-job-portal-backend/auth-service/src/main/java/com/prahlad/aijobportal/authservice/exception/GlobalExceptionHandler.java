package com.prahlad.aijobportal.authservice.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import com.prahlad.aijobportal.common.response.ApiError;
import com.prahlad.aijobportal.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Centralized exception translation for the Auth Service. Ensures every
 * error response — validation failures, business rule violations, and
 * unexpected errors alike — uses the same {@link ApiResponse} envelope and
 * never leaks a raw stack trace to the client.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        log.warn("Business exception [{}]: {}", ex.getErrorCode(), ex.getMessage());
        ApiError error = ApiError.builder()
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ApiResponse.failure(ex.getMessage(), List.of(error)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ApiError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toApiError)
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("Validation failed", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        List<ApiError> errors = ex.getConstraintViolations().stream()
                .map(violation -> ApiError.builder()
                        .field(violation.getPropertyPath().toString())
                        .code("FIELD_INVALID")
                        .message(violation.getMessage())
                        .build())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("Validation failed", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        ApiError error = ApiError.builder()
                .code("MALFORMED_REQUEST_BODY")
                .message("The request body is missing or malformed")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("Malformed request body", List.of(error)));
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(AuthenticationException ex) {
        ApiError error = ApiError.builder()
                .code("INVALID_CREDENTIALS")
                .message("Invalid email or password")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure("Invalid email or password", List.of(error)));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        ApiError error = ApiError.builder()
                .code("ACCESS_DENIED")
                .message("You do not have permission to perform this action")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.failure("Access denied", List.of(error)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception ex) {
        log.error("Unexpected error", ex);
        ApiError error = ApiError.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Internal server error", List.of(error)));
    }

    private ApiError toApiError(FieldError fieldError) {
        return ApiError.builder()
                .field(fieldError.getField())
                .code("FIELD_INVALID")
                .message(fieldError.getDefaultMessage())
                .build();
    }
}
