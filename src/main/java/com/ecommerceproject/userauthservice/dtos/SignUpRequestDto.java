package com.ecommerceproject.userauthservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDto {
    private String username;

    private String email;

    private String password;
}
