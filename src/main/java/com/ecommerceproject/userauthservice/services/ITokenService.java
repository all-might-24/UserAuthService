package com.ecommerceproject.userauthservice.services;

import java.util.Map;

public interface ITokenService {

    String generateToken(Map<String, Object> payload);

    boolean validateToken(String token);

    Long getUserId(String token);
}
