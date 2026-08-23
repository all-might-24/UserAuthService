package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.dtos.UserDto;
import com.ecommerceproject.userauthservice.exceptions.RoleDoesNotExistException;
import com.ecommerceproject.userauthservice.exceptions.UserDoesNotExistsException;
import com.ecommerceproject.userauthservice.mapper.UserMapper;
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

    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
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
    public void assignRoles(Long userId, List<String> roleNames) {

        Optional<User> optionalUser = findByUserId(userId);

        if (optionalUser.isEmpty()) {
            throw new UserDoesNotExistsException("User does not exist");
        }

        User user = optionalUser.get();

        for (String roleName : roleNames) {

            Optional<Role> optionalRole =
                    roleRepository.findByRoleTitle(roleName);

            if (optionalRole.isEmpty()) {
                throw new RoleDoesNotExistException("Role does not exist");
            }

            Role role = optionalRole.get();

            if (!user.getRoles().contains(role)) {
                user.getRoles().add(role);
            }
        }

        userRepository.save(user);
    }

    @Override
    public UserDto getUserInfo(Long userId) {
        Optional<User> optionalUser = findByUserId(userId);
        if(optionalUser.isEmpty()) {
            throw new UserDoesNotExistsException("User does not exist");
        }
        User user = optionalUser.get();
        return convertToDto(user);
    }

    @Override
    public List<String> getMyProfileRoles(Long userId) {
        Optional<User> optionalUser = findByUserId(userId);
        if(optionalUser.isEmpty()) {
            throw new UserDoesNotExistsException("User does not exist");
        }

        return optionalUser.get()
                .getRoles()
                .stream()
                .map(Role::getRoleTitle)
                .toList();
    }

    @Override
    public UserDto convertToDto(User user) {
        return userMapper.toDto(user);
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
        return userRepository.findById(userId);
    }

}
