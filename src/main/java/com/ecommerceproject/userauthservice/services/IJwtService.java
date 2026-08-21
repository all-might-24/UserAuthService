package com.ecommerceproject.userauthservice.services;

import java.util.Map;

public interface IJwtService {

    String generateJwtToken(Map<String, Object> payload);

    boolean validateToken(String token);
}
