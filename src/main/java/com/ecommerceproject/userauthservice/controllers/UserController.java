package com.ecommerceproject.userauthservice.controllers;

import com.ecommerceproject.userauthservice.dtos.UpdateRolesRequestDto;
import com.ecommerceproject.userauthservice.dtos.UserDto;
import com.ecommerceproject.userauthservice.services.IUserService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateRoles(@PathVariable Long userId, @Valid @RequestBody UpdateRolesRequestDto updateRolesRequestDto) {
        userService.assignRoles(userId, updateRolesRequestDto.getRoleNames());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyProfile(Authentication authentication) {
        UserDto user = userService.getUserInfo((Long) authentication.getPrincipal());
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/me/roles")
    public ResponseEntity<List<String>> getMyProfileRoles(Authentication authentication) {
        List<String> userRoles = userService.getMyProfileRoles((Long) authentication.getPrincipal());
        return ResponseEntity.ok().body(userRoles);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable Long userId) {
        UserDto userDto = userService.getUserInfo(userId);
        return ResponseEntity.ok().body(userDto);
    }

}
