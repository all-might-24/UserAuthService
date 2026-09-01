package com.ecommerceproject.userauthservice.controllers;

import com.ecommerceproject.userauthservice.exceptions.UnAuthorizedException;
import com.ecommerceproject.userauthservice.services.ISessionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/session")
public class SessionController {

    private final ISessionService sessionService;

    public SessionController(ISessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateSession(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        String bearer = "Bearer ";

        if (authHeader == null || !authHeader.startsWith(bearer)) {
            throw new UnAuthorizedException("Unauthorized Access");
        }

        String token = authHeader.substring(bearer.length());

        if (!sessionService.isSessionValid(token)) {
            throw new UnAuthorizedException("Session is inactive");
        }

        return ResponseEntity.ok().build();
    }
}