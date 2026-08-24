package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.dtos.UserDto;
import com.ecommerceproject.userauthservice.dtos.UserTokenDto;
import com.ecommerceproject.userauthservice.exceptions.EmailAlreadyExistsException;
import com.ecommerceproject.userauthservice.exceptions.InvalidCredentialsException;
import com.ecommerceproject.userauthservice.exceptions.UserDoesNotExistsException;
import com.ecommerceproject.userauthservice.models.Role;
import com.ecommerceproject.userauthservice.models.User;
import com.ecommerceproject.userauthservice.models.enums.State;
import com.ecommerceproject.userauthservice.repositories.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private JwtTokenService jwtService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;


    @Test
    void signup_shouldReturnUserDto_whenCredentialsAreValid() {

        String userName = "Test12";
        String email = "test12@example.com";
        String password = "test123";
        String encodedPass = "encodedPass";

        Role role = new Role();
        role.setRoleTitle("USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword(encodedPass);
        user.setState(State.ACTIVE);
        user.setRoles(roles);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail(email);
        userDto.setUsername(userName);
        userDto.setRoles(
                roles
                .stream()
                .map(Role::getRoleTitle)
                .toList()
        );

        when(userService.findByEmail(email))
                .thenReturn(Optional.empty());

        when(encoder.encode(password))
                .thenReturn(encodedPass);

        when(roleRepository.findByRoleTitle("USER"))
                .thenReturn(Optional.of(role));

        when(userService.createUser(any(User.class)))
                .thenReturn(user);

        when(userService.convertToDto(user))
                .thenReturn(userDto);

        UserDto result = authService.signup(userName, email, password);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(userName, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals(roles.stream().map(Role::getRoleTitle).toList(), result.getRoles());
    }


    @Test
    void login_shouldReturnUserTokenDto_whenCredentialsAreValid() {

        String userName = "Test12";
        String email = "test12@example.com";
        String password = "test123";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword(password);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail(email);
        userDto.setUsername(userName);

        when(userService.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(encoder.matches(password, "test123"))
                .thenReturn(true);

        when(jwtService.generateToken(anyMap()))
                .thenReturn("fake-jwt-token");

        when(userService.convertToDto(user))
                .thenReturn(userDto);

        UserTokenDto result = authService.login(email, password);

        assertNotNull(result);
        assertEquals("fake-jwt-token", result.getToken());
        assertEquals(userDto, result.getUser());
    }

    @Test
    void signup_shouldThrowEmailAlreadyExistsException_whenEmailIsPresent() {

        String userName = "Test12";
        String email = "test12@example.com";
        String password = "test123";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword(password);

        when(userService.findByEmail(email))
                .thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistsException.class, () -> authService.signup(userName, email, password));
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenPasswordIsWrong() {

        String email = "test12@example.com";
        String password = "test123";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword(password);

        when(userService.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(encoder.matches(password, "test123"))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(email, password));
    }

    @Test
    void login_shouldThrowUserDoesNotExistsException_whenEmailDoesNotExist() {

        String email = "test12@example.com";
        String password = "test123";

        when(userService.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistsException.class, () -> authService.login(email, password));
    }


    @Test
    void signup_shouldThrowEmailAlreadyExistsException_whenEmailAlreadyExists() {

        String email = "test12@example.com";

        User existingUser = new User();
        existingUser.setEmail(email);

        when(userService.findByEmail(email))
                .thenReturn(Optional.of(existingUser));

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.signup(
                        "Test12",
                        email,
                        "test123"
                )
        );

        verify(encoder, never()).encode(anyString());
        verify(roleRepository, never()).findByRoleTitle(anyString());
        verify(userService, never()).createUser(any(User.class));
    }


    @Test
    void signup_shouldCreateUserRole_whenUserRoleDoesNotExist() {

        String username = "Test12";
        String email = "test12@example.com";
        String password = "test123";

        Role savedRole = new Role();
        savedRole.setId(1L);
        savedRole.setRoleTitle("USER");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail(email);
        userDto.setUsername(username);
        userDto.setRoles(Set.of("USER").stream().toList());

        when(userService.findByEmail(email))
                .thenReturn(Optional.empty());

        when(encoder.encode(password))
                .thenReturn("encodedPassword");

        when(roleRepository.findByRoleTitle("USER"))
                .thenReturn(Optional.empty());

        when(roleRepository.save(any(Role.class)))
                .thenReturn(savedRole);

        when(userService.createUser(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(userService.convertToDto(any(User.class)))
                .thenReturn(userDto);

        UserDto result = authService.signup(username, email, password);

        assertNotNull(result);
        assertEquals("USER", result.getRoles().get(0));

        verify(roleRepository).save(any(Role.class));
        verify(userService).createUser(any(User.class));
    }
}