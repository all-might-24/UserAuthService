package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.dtos.UserDto;
import com.ecommerceproject.userauthservice.dtos.UserTokenDto;
import com.ecommerceproject.userauthservice.exceptions.*;
import com.ecommerceproject.userauthservice.models.Role;
import com.ecommerceproject.userauthservice.models.Session;
import com.ecommerceproject.userauthservice.models.User;
import com.ecommerceproject.userauthservice.models.enums.State;
import com.ecommerceproject.userauthservice.repositories.RoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AuthService implements IAuthService{

    private final RoleRepository roleRepository;

    private final ISessionService sessionService;

    private final BCryptPasswordEncoder encoder;

    private final ITokenService jwtService;

    private final IUserService userService;



    public AuthService(RoleRepository roleRepository,
                       ISessionService sessionService,
                       BCryptPasswordEncoder encoder,
                       ITokenService jwtService,
                       IUserService userService) {
        this.roleRepository = roleRepository;
        this.sessionService = sessionService;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.userService = userService;
    }



    @Override
    public UserDto signup(String name, String email, String password) {

        Optional<User> optionalUser = userService.findByEmail(email);
        if(optionalUser.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exits, Please enter another one");
        }

        User user = new User();

        user.setUsername(name);
        user.setEmail(email);

        user.setPassword(encoder.encode(password));
        user.setState(State.ACTIVE);

        Optional<Role> optionalRole = roleRepository.findByRoleTitle("USER");
        Role roleToBeSet;

        if(optionalRole.isEmpty()) {
            Role role = new Role();
            role.setRoleTitle("USER");
            roleRepository.save(role);
            roleToBeSet = role;
        } else {
            roleToBeSet = optionalRole.get();
        }
        user.setRoles(List.of(roleToBeSet));

        return userService.convertToDto(userService.createUser(user));
    }

    @Override
    public UserTokenDto login(String email, String password) {
        Optional<User> optionalUser = userService.findByEmail(email);

        if(optionalUser.isEmpty()) {
            throw new UserDoesNotExistsException("User does not exists");
        }

        User user = optionalUser.get();

        if(encoder.matches(password, user.getPassword())) { // matches(rawPassword, EncodedPassword)

            Map<String, Object> payload = new HashMap<>();
            long currentTimeInMills = System.currentTimeMillis();

            payload.put("iat", currentTimeInMills);
            payload.put("exp", currentTimeInMills+10000000);
            payload.put("iss", "Issuer");
            payload.put("userId", user.getId());

            List<String> roles = user.getRoles().stream().map(Role::getRoleTitle).toList();
            payload.put("scope", roles);

            String token = jwtService.generateToken(payload);
            //System.out.println("Token : " + token);
            Session session = new Session();
            session.setUser(user);
            session.setToken(token);
            session.setState(State.ACTIVE);

            sessionService.saveSession(session);

            return new UserTokenDto(userService.convertToDto(user), token);
        } else {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
    }

    @Override
    public boolean validateToken(String token) {

        Optional<Session> optionalSession = sessionService.findByToken(token);

        if(optionalSession.isEmpty()) {
            return false;
        }

        if (!jwtService.validateToken(token)) {
            Session session = optionalSession.get();
            session.setState(State.INACTIVE);
            sessionService.saveSession(session);
            return false;
        }

        return true;
    }

    @Override
    public void logout(String authHeader) {
        sessionService.logout(authHeader);
    }

}

/*
    Theory:-

    We will use BCryptPasswordEncoder for encoding them. BCryptPasswordEncoder is a Spring Security utility used to hash (encode) passwords securely before storing them and to verify passwords during login.

    BCrypt is preferred because it uses strong one-way hashing (cannot be reversed) and automatically adds a random salt.

    1. One-Way Hashing: Once the hash is created, it cannot be reversed to get the original value back.
    2. Salting: BCrypt automatically adds a random salt.
            Analogy: Think of salt as adding one more layer of security on your hashed value to prevent pre-computed attacks (rainbow tables).
    3. Cost Factor: This refers to the number of hashing rounds. More rounds = more secure, but more CPU intensive.

    1. Hashing, encoding, encryption

    java doesn't even run on OS kernel

    Java runs on JVM
 */