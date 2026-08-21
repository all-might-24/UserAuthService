package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.models.User;
import com.ecommerceproject.userauthservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService{

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
