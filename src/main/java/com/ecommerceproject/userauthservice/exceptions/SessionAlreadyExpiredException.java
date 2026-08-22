package com.ecommerceproject.userauthservice.exceptions;

public class SessionAlreadyExpiredException extends RuntimeException {
    public SessionAlreadyExpiredException(String message) {
        super(message);
    }
}
