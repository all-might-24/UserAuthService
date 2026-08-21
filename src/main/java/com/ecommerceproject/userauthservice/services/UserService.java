package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.dtos.UserDto;
import com.ecommerceproject.userauthservice.mapper.UserMapper;
import com.ecommerceproject.userauthservice.models.Role;
import com.ecommerceproject.userauthservice.models.User;
import com.ecommerceproject.userauthservice.models.enums.State;
import com.ecommerceproject.userauthservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService{

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Override
    public UserDto convertToDto(User user) {
        return userMapper.toDto(user);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

//    @Override
//    public User findById(long id) {
//        return null;
//    }


//    @Override
//    public void updateProfile(long id, User user) {
//
//    }
//
//    @Override
//    public void changeState(long id, State state) {
//
//    }
//
//    @Override
//    public void assignRole(long id, List<Role> roles) {
//
//    }
//
//    @Override
//    public void deleteUser(long id) {
//
//    }
}
