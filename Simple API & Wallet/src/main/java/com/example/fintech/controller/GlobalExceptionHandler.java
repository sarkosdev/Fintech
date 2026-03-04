package com.example.fintech.controller;

import com.example.fintech.dto.ErrorResponse;
import com.example.fintech.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * Handles BusinessException exceptions, which are thrown when a business rule
     * is violated (e.g., insufficient balance or transferring money to the same account).
     *
     * @param ex The captured exception instance containing the error message.
     * @return A ResponseEntity containing the ErrorResponse object and HTTP status 400 (Bad Request).
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<ErrorResponse> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Validation failed",
                HttpStatus.BAD_REQUEST.value(),
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Generic handler for any other unexpected exceptions (RuntimeExceptions, etc.).
     *
     * This acts as a "safety net" to prevent the server from exposing the full
     * stack trace, which could represent a security risk (Information Exposure).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                "Unexpected server error",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
