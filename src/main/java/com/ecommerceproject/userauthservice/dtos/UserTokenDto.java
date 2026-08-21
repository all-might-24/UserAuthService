package com.ecommerceproject.userauthservice.dtos;

import com.ecommerceproject.userauthservice.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserTokenDto {
    private User user;
    private String token;
}
