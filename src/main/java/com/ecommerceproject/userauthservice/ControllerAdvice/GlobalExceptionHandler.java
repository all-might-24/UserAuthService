package com.ecommerceproject.userauthservice.ControllerAdvice;

import com.ecommerceproject.userauthservice.dtos.ExceptionDto;
import com.ecommerceproject.userauthservice.exceptions.EmailAlreadyExistsException;
import com.ecommerceproject.userauthservice.exceptions.InvalidCredentialsException;
import com.ecommerceproject.userauthservice.exceptions.UserDoesNotExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ExceptionDto> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        ExceptionDto edto = new ExceptionDto();
        edto.setMessage(e.getMessage());
        return new ResponseEntity<>(edto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserDoesNotExistsException.class)
    public ResponseEntity<ExceptionDto> handleUserDoesNotExistsException(UserDoesNotExistsException e) {
        ExceptionDto edto = new ExceptionDto();
        edto.setMessage(e.getMessage());
        return new ResponseEntity<>(edto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ExceptionDto> handleInvalidCredentialsException(InvalidCredentialsException e) {
        ExceptionDto edto = new ExceptionDto();
        edto.setMessage(e.getMessage());
        return new ResponseEntity<>(edto, HttpStatus.UNAUTHORIZED);
    }
}
