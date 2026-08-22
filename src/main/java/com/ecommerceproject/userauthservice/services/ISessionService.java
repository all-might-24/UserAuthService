package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.models.Session;

import java.util.Optional;

public interface ISessionService {
    boolean isSessionValid(String token);
    Optional<Session> findByToken(String token);
    void saveSession(Session session);
    void logout(String authHeader);
}
