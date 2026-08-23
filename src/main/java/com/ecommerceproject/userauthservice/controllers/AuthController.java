package com.ecommerceproject.userauthservice.controllers;

import com.ecommerceproject.userauthservice.dtos.*;
import com.ecommerceproject.userauthservice.exceptions.UnAuthorizedException;
import com.ecommerceproject.userauthservice.services.IAuthService;
import com.ecommerceproject.userauthservice.services.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        UserDto user = authService.signup(signUpRequestDto.getUsername(),
                signUpRequestDto.getEmail(),
                signUpRequestDto.getPassword());

        //return new ResponseEntity<>(user, HttpStatus.CREATED);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);

    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {

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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.noContent().build();
    }

    /* --------------------------------------------------------------------------------------

    @GetMapping("/admin-test")
    @PreAuthorize("hasRole('ADMIN')")
    public String testAdmin() {
        return "Welcome Admin";
    }

    @GetMapping("/user-test")
    @PreAuthorize("hasRole('USER')")
    public String testUser() {
        return "Welcome User";
    }

    @GetMapping("/test/{id}")
    public String testValidation(@PathVariable @Positive Long id) {
        return "Valid ID: " + id;
    }
    @GetMapping("/test-auth")
    public String testAuthentication(Authentication authentication) {
        if (authentication == null) {
            return "Not authenticated";
        }
        return "Authenticated user: " + authentication.getPrincipal() +
                ", Authorities: " + authentication.getAuthorities();
    }


     */
}
