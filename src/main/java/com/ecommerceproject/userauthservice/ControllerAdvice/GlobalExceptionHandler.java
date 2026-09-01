package com.ecommerceproject.userauthservice.ControllerAdvice;

import com.ecommerceproject.userauthservice.dtos.ExceptionDto;
import com.ecommerceproject.userauthservice.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ExceptionDto> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionDto dto = createExceptionDto(status, e.getMessage());
        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(UserDoesNotExistsException.class)
    public ResponseEntity<ExceptionDto> handleUserDoesNotExistsException(UserDoesNotExistsException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionDto dto = createExceptionDto(status, e.getMessage());
        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ExceptionDto> handleInvalidCredentialsException(InvalidCredentialsException e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ExceptionDto dto = createExceptionDto(status, e.getMessage());
        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ExceptionDto> handleUnAuthorizedException(UnAuthorizedException e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ExceptionDto dto = createExceptionDto(status, e.getMessage());
        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(RoleDoesNotExistException.class)
    public ResponseEntity<ExceptionDto> handleRoleDoesNotExistException(RoleDoesNotExistException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionDto dto = createExceptionDto(status, e.getMessage());
        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(SessionDoesNotExistException.class)
    public ResponseEntity<ExceptionDto> handleSessionDoesNotExistException(SessionDoesNotExistException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionDto dto = createExceptionDto(status, e.getMessage());
        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(SessionAlreadyExpiredException.class)
    public ResponseEntity<ExceptionDto> handleSessionAlreadyExpiredException(SessionAlreadyExpiredException e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ExceptionDto dto = createExceptionDto(status, e.getMessage());
        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage(),
                        (firstMessage, secondMessage) -> firstMessage
                ));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionDto dto = createExceptionDto(status, "Validation Failed");
        dto.setErrors(errors);
        return ResponseEntity
                .status(status)
                .body(dto);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDto> handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> errors = e
                .getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        error -> {
                            String path = error.getPropertyPath().toString();
                            return path.substring(path.lastIndexOf('.') + 1);
                        },
                        error -> error.getMessage()
                ));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionDto dto = createExceptionDto(status, "Validation Failed");
        dto.setErrors(errors);
        return new ResponseEntity<>(dto, status);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ExceptionDto> handleUnexpectedException(Exception e) {
//        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
//        ExceptionDto dto = createExceptionDto(status, "Unexpected error has occurred");
//        return ResponseEntity
//                .status(status)
//                .body(dto);
//    }

    private ExceptionDto createExceptionDto(HttpStatus status, String message) {
        ExceptionDto dto = new ExceptionDto();

        dto.setStatus(status.value());
        dto.setMessage(message);
        dto.setTimeStamp(LocalDateTime.now());

        return dto;
    }
}
