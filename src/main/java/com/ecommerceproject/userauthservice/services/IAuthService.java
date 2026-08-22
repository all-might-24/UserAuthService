package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.dtos.UserDto;
import com.ecommerceproject.userauthservice.dtos.UserTokenDto;


public interface IAuthService {

    UserDto signup(String name, String email, String password);

    UserTokenDto login(String email, String password);

    boolean validateToken(String token);
}
