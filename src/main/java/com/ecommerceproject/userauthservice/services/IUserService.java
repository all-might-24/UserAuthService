package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.dtos.UserDto;
//import com.ecommerceproject.userauthservice.models.Role;
import com.ecommerceproject.userauthservice.models.User;
//import com.ecommerceproject.userauthservice.models.enums.State;

//import java.util.List;
import java.util.Optional;

public interface IUserService {

    Optional<User> findByEmail(String email);

//    User findById(long id);

    UserDto convertToDto(User user);

    User createUser(User user);

//    void updateProfile(long id, User user);
//
//    void changeState(long id, State state);
//
//    void assignRole(long id, List<Role> roles);
//
//    void deleteUser(long id);

}
