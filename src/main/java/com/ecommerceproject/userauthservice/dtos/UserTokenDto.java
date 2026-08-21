package com.ecommerceproject.userauthservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserTokenDto {
    private UserDto user;
    private String token;
}
