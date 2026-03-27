package com.example.fintech.exception;

/**
 * Custom Business Exception class interceptor
 */
public class BusinessException extends RuntimeException{

    // Custom Exception used for general Transactions errors
    public BusinessException(String message) {
        super(message);
    }

}
