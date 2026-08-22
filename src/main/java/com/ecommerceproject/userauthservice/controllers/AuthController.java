package com.ecommerceproject.userauthservice.controllers;

import com.ecommerceproject.userauthservice.dtos.*;
import com.ecommerceproject.userauthservice.exceptions.UnAuthorizedException;
import com.ecommerceproject.userauthservice.models.User;
import com.ecommerceproject.userauthservice.services.IAuthService;
import com.ecommerceproject.userauthservice.services.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;

    private final IUserService userService;

//    public AuthController(IAuthService authService) {
//        this.authService = authService;
//    }

    public AuthController(IAuthService authService, IUserService userService) {
        this.authService = authService;
        this.userService = userService;
    }


    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
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
        return "Authenticated user: " + authentication.getPrincipal() +
                ", Authorities: " + authentication.getAuthorities();
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return "Logout success";
    }

    @PostMapping("/assign-roles")
    @PreAuthorize("hasRole('ADMIN')")
    public void assignRolesToUser(@RequestBody AssignRolesRequestDto assignRolesRequestDto) {
        userService.assignRoles(assignRolesRequestDto.getUser().getEmail(), assignRolesRequestDto.getRoles());
    }

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
}
