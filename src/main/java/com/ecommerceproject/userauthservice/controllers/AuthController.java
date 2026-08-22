package com.ecommerceproject.userauthservice.controllers;

import com.ecommerceproject.userauthservice.dtos.*;
import com.ecommerceproject.userauthservice.exceptions.UnAuthorizedException;
import com.ecommerceproject.userauthservice.services.IAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        UserDto user = authService.signup(signUpRequestDto.getUsername(),
                signUpRequestDto.getEmail(),
                signUpRequestDto.getPassword());

        return new ResponseEntity<>(user, HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {

        UserTokenDto userTokenDto = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, userTokenDto.getToken());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(userTokenDto.getUser());
    }

    @PostMapping("/validate-token")
    public void validateToken(@RequestBody ValidateTokenDto validateTokenDto) {
        boolean isValidToken = authService.validateToken(validateTokenDto.getToken());

        if(!isValidToken) {
            throw new UnAuthorizedException("Invalid Token");
        }
    }

    @GetMapping("/test-auth")
    public String testAuthentication(Authentication authentication) {
        if (authentication == null) {
            return "Not authenticated";
        }
        return "Authenticated user: " + authentication.getPrincipal();
    }
}
