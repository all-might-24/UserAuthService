package com.ecommerceproject.userauthservice.exceptions;

public class UserDoesNotExistsException extends RuntimeException{

    public UserDoesNotExistsException(String message) {
        super(message);
    }
}
