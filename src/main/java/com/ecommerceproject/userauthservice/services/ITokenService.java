package com.ecommerceproject.userauthservice.services;

import java.util.List;
import java.util.Map;

public interface ITokenService {

    String generateToken(Map<String, Object> payload);

    boolean validateToken(String token);

    Long getUserId(String token);

    List<String> getUserRoles(String token);
}
