package com.example.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ErrorResponse class - Data Transfer Object
 */
@Getter                                             // Can only access getters(), no setters() defined
@AllArgsConstructor                                 // Constructor with all class fields
public class ErrorResponse {

    // Used for other Error Responses
    public ErrorResponse(LocalDateTime timestamp, String message, int status) {
        this.timestamp = timestamp;
        this.message = message;
        this.status = status;
    }

    private LocalDateTime timestamp;
    private String message;
    private int status;
    private Map<String, String> errors;

}
