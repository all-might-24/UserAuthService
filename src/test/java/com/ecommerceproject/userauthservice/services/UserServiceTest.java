package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.dtos.UserDto;
import com.ecommerceproject.userauthservice.exceptions.RoleDoesNotExistException;
import com.ecommerceproject.userauthservice.exceptions.UserDoesNotExistsException;
import com.ecommerceproject.userauthservice.mapper.UserMapper;
import com.ecommerceproject.userauthservice.models.Role;
import com.ecommerceproject.userauthservice.models.User;
import com.ecommerceproject.userauthservice.repositories.RoleRepository;
import com.ecommerceproject.userauthservice.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;


    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {

        String email = "john@email.com";

        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());

        verify(userRepository).findByEmail(email);
    }


    @Test
    void createUser_shouldReturnSavedUser() {

        User user = new User();
        user.setEmail("john@email.com");

        when(userRepository.save(user))
                .thenReturn(user);

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals(user, result);

        verify(userRepository).save(user);
    }


    @Test
    void getUserInfo_shouldReturnUserDto_whenUserExists() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("john");

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setUsername("john");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userMapper.toDto(user))
                .thenReturn(userDto);

        UserDto result = userService.getUserInfo(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("john", result.getUsername());
    }


    @Test
    void getUserInfo_shouldThrowUserDoesNotExistsException_whenUserDoesNotExist() {

        Long userId = 999L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserDoesNotExistsException.class,
                () -> userService.getUserInfo(userId)
        );

        verify(userMapper, never()).toDto(any(User.class));
    }


    @Test
    void getMyProfileRoles_shouldReturnRoles_whenUserExists() {

        Long userId = 1L;

        Role userRole = new Role();
        userRole.setRoleTitle("USER");

        Role adminRole = new Role();
        adminRole.setRoleTitle("ADMIN");

        User user = new User();
        user.setId(userId);
        user.setRoles(Set.of(userRole, adminRole));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        List<String> result = userService.getMyProfileRoles(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("USER"));
        assertTrue(result.contains("ADMIN"));
    }


    @Test
    void getMyProfileRoles_shouldThrowUserDoesNotExistsException_whenUserDoesNotExist() {

        Long userId = 999L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserDoesNotExistsException.class,
                () -> userService.getMyProfileRoles(userId)
        );
    }


    @Test
    void assignRoles_shouldAddRolesToUser_whenUserAndRolesExist() {

        Long userId = 1L;

        Role adminRole = new Role();
        adminRole.setRoleTitle("ADMIN");

        User user = new User();
        user.setId(userId);
        user.setRoles(new java.util.HashSet<>());

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(roleRepository.findByRoleTitle("ADMIN"))
                .thenReturn(Optional.of(adminRole));

        userService.assignRoles(
                userId,
                List.of("ADMIN")
        );

        assertTrue(user.getRoles().contains(adminRole));

        verify(userRepository).save(user);
        verify(roleRepository).findByRoleTitle("ADMIN");
    }


    @Test
    void assignRoles_shouldThrowUserDoesNotExistsException_whenUserDoesNotExist() {

        Long userId = 999L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserDoesNotExistsException.class,
                () -> userService.assignRoles(
                        userId,
                        List.of("ADMIN")
                )
        );

        verify(roleRepository, never()).findByRoleTitle(anyString());
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void assignRoles_shouldThrowRoleDoesNotExistException_whenRoleDoesNotExist() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setRoles(new java.util.HashSet<>());

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(roleRepository.findByRoleTitle("INVALID"))
                .thenReturn(Optional.empty());

        assertThrows(
                RoleDoesNotExistException.class,
                () -> userService.assignRoles(
                        userId,
                        List.of("INVALID")
                )
        );

        verify(userRepository, never()).save(any(User.class));
    }
}