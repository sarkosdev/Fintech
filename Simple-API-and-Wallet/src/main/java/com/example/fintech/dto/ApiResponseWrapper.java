package com.example.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ApiResponseWrapper Class meant for API responses
 */
@Data
@AllArgsConstructor
public class ApiResponseWrapper {

    private boolean success;
    private String message;
    private LocalDateTime timestamp;

    /**
     * Success case scenario
     * @param message
     * @return ApiResponseWrapper
     */
    public static ApiResponseWrapper success(String message){
        return new ApiResponseWrapper(true, message, LocalDateTime.now());
    }

    /**
     * Error case scenario
     * @param message
     * @return ApiResponseWrapper
     */
    public static ApiResponseWrapper error(String message){
        return new ApiResponseWrapper(false, message, LocalDateTime.now());
    }

}
