package com.ecommerceproject.userauthservice.controllers;

import com.ecommerceproject.userauthservice.dtos.*;
import com.ecommerceproject.userauthservice.exceptions.UnAuthorizedException;
import com.ecommerceproject.userauthservice.models.User;
import com.ecommerceproject.userauthservice.services.IAuthService;
import com.ecommerceproject.userauthservice.services.IJwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService, IJwtService jwtService) {
        this.authService = authService;

    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        User user = authService.signup(signUpRequestDto.getUsername(),
                signUpRequestDto.getEmail(),
                signUpRequestDto.getPassword());

        return new ResponseEntity<>(user.convertToDTO(), HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {

        UserTokenDto userTokenDto = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, userTokenDto.getToken());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(userTokenDto.getUser().convertToDTO());
    }

    @PostMapping("/validate-token")
    public void validateToken(@RequestBody ValidateTokenDto validateTokenDto) {
        boolean isValidToken = authService.validateToken(validateTokenDto.getToken());

        if(!isValidToken) {
            throw new UnAuthorizedException("Invalid Token");
        }
    }
}
