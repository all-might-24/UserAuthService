package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.dtos.UserDto;
import com.ecommerceproject.userauthservice.models.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    Optional<User> findByEmail(String email);

    User createUser(User user);

    void assignRoles(Long userId, List<String> roleNames);

//    UserDto getMyProfile(Long userId);

    UserDto convertToDto(User user);

    List<String> getMyProfileRoles(Long userId);

    Optional<User> findByUserId(Long userId);

    UserDto getUserInfo(Long userId);
}
