package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.exceptions.RoleDoesNotExistException;
import com.ecommerceproject.userauthservice.exceptions.UserDoesNotExistsException;
import com.ecommerceproject.userauthservice.models.Role;
import com.ecommerceproject.userauthservice.models.User;
import com.ecommerceproject.userauthservice.repositories.RoleRepository;
import com.ecommerceproject.userauthservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService{

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void assignRoles(String email, List<Role> roles) {

        Optional<User> optionalUser = findByEmail(email);
        if(optionalUser.isEmpty()) {
            throw new UserDoesNotExistsException("User does not exist");
        }
        if(roles.isEmpty()) return;

        User user = optionalUser.get();

        for(Role irole : roles) {
            Optional<Role> optionalRole = roleRepository.findByRoleTitle(irole.getRoleTitle());
            if(optionalRole.isEmpty()) {
                throw new RoleDoesNotExistException("Role does not exist");
            }
            Role role = optionalRole.get();

            if(!user.getRoles().contains(role)) {
                user.getRoles().add(role);
            }
        }
        userRepository.save(user);
    }


}
