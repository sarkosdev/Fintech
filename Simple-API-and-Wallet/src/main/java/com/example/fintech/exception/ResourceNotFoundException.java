package com.example.fintech.exception;

/**
 * Custom Resource Exception class interceptor
 */
public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message){super(message);}
}
