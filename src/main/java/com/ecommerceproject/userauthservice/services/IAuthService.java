package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.dtos.UserTokenDto;
import com.ecommerceproject.userauthservice.models.User;

public interface IAuthService {

    User signup(String name, String email, String password);

    UserTokenDto login(String email, String password);

    Boolean validateUserToken(String token);

}
