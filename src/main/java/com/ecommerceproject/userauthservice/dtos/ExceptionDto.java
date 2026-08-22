package com.ecommerceproject.userauthservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExceptionDto {
    private int status;
    private String message;
    private LocalDateTime timeStamp;
}
