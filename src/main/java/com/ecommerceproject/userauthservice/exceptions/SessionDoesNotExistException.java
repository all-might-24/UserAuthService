package com.ecommerceproject.userauthservice.exceptions;

public class SessionDoesNotExistException extends RuntimeException{
    public SessionDoesNotExistException(String message) {
        super(message);
    }
}
