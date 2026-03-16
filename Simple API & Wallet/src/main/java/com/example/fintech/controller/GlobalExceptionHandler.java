package com.example.fintech.controller;

import com.example.fintech.dto.ApiResponseWrapper;
import com.example.fintech.dto.ErrorResponse;
import com.example.fintech.exception.BusinessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * This class acts as a global interceptor for exceptions thrown by any Controller.
 *
 * It uses the @RestControllerAdvice annotation, which combines @ControllerAdvice
 * and @ResponseBody, ensuring that error responses are automatically serialized
 * into JSON format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);


    /**
     * Handles BusinessException exceptions, which are thrown when a business rule
     * is violated (e.g., insufficient balance or transferring money to the same account).
     *
     * @param ex The captured exception instance containing the error message.
     * @return A ResponseEntity containing the ErrorResponse object and HTTP status 400 (Bad Request).
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseWrapper> handleBusinessException(BusinessException ex) {

        logger.error("Unexpected error occurred 3", ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseWrapper.error(ex.getMessage()));
    }

    /**
     * Handles argument validation exceptions (MethodArgumentNotValidException).
     *
     * These exceptions are automatically triggered by Spring when validation
     * annotations (such as @NotNull, @Email, or @Positive) fail in input DTOs.
     *
     * @param ex Validation exception triggered by Bean Validation.
     * @return Structured error response with HTTP status 400.
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseWrapper> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {

        logger.error("Unexpected error occurred 2", ex);

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        String message = "Validation failed: " + errors;

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseWrapper.error(message));
    }

    /**
     * Bad Credentials handler in case user input wrong credentials
     * This act as a way to show user that the credentials are wrong and there was a problem trying to login
     * @param ex
     * @return ApiResponseWrapper
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseWrapper> handleBadCredentials(BadCredentialsException ex) {

        logger.warn("Authentication failed: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseWrapper.error("Invalid email or password"));
    }


    /**
     * Generic handler for any other unexpected exceptions (RuntimeExceptions, etc.).
     *
     * This acts as a "safety net" to prevent the server from exposing the full
     * stack trace, which could represent a security risk (Information Exposure).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper> handleGeneralException(Exception ex) {

        logger.error("Unexpected error occurred 1: " + ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.error(ex.getMessage()));
    }

}
