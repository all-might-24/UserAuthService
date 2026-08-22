package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.models.Role;
import com.ecommerceproject.userauthservice.models.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    Optional<User> findByEmail(String email);

    User createUser(User user);

    void assignRoles(String email, List<Role> roles);
}
