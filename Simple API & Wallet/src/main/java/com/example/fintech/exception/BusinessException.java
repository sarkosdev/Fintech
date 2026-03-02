package com.example.fintech.exception;

public class BusinessException extends RuntimeException{

    // Custom Exception used for not enough balance on your Account
    public BusinessException(String message) {
        super(message);
    }

}
