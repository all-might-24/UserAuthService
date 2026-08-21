package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.dtos.UserTokenDto;
import com.ecommerceproject.userauthservice.exceptions.EmailAlreadyExistsException;
import com.ecommerceproject.userauthservice.exceptions.InvalidCredentialsException;
import com.ecommerceproject.userauthservice.exceptions.UserDoesNotExistsException;
import com.ecommerceproject.userauthservice.mapper.UserMapper;
import com.ecommerceproject.userauthservice.models.Role;
import com.ecommerceproject.userauthservice.models.Session;
import com.ecommerceproject.userauthservice.models.User;
import com.ecommerceproject.userauthservice.models.enums.State;
import com.ecommerceproject.userauthservice.repositories.RoleRepository;
import com.ecommerceproject.userauthservice.repositories.SessionRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AuthService implements IAuthService{

    private final RoleRepository roleRepository;

    private final SessionRepository sessionRepository;

    private final BCryptPasswordEncoder encoder;

    private final ITokenService jwtService;

    private final IUserService userService;



    public AuthService(RoleRepository roleRepository,
                       SessionRepository sessionRepository,
                       BCryptPasswordEncoder encoder,
                       ITokenService jwtService,
                       IUserService userService) {
        this.roleRepository = roleRepository;
        this.sessionRepository = sessionRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.userService = userService;
    }



    @Override
    public User signup(String name, String email, String password) {

        Optional<User> optionalUser = userService.findByEmail(email);
        if(optionalUser.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exits, Please enter another one");
        }

        User user = new User();

        user.setUsername(name);
        user.setEmail(email);

        user.setPassword(encoder.encode(password));
        user.setState(State.ACTIVE);

        Optional<Role> optionalRole = roleRepository.findByRoleTitle("DEFAULT");
        Role roleToBeSet;

        if(optionalRole.isEmpty()) {
            Role role = new Role();
            role.setRoleTitle("DEFAULT");
            roleRepository.save(role);
            roleToBeSet = role;
        } else {
            roleToBeSet = optionalRole.get();
        }
        user.setRoles(List.of(roleToBeSet));

        return userService.createUser(user);
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
            payload.put("exp", currentTimeInMills+100000);
            payload.put("iss", "Issuer");
            payload.put("userId", user.getUsername());
            payload.put("scope", user.getRoles());

            //MacAlgorithm macAlgorithm = Jwts.SIG.HS256;
            //SecretKey secretKey = macAlgorithm.key().build();

            String token = jwtService.generateToken(payload);
            //System.out.println("Token : " + token);
            Session session = new Session();
            session.setUser(user);
            session.setToken(token);
            session.setState(State.ACTIVE);

            sessionRepository.save(session);

            return new UserTokenDto(userService.convertToDto(user), token);
        } else {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
    }

    @Override
    public boolean validateToken(String token) {

        Optional<Session> optionalSession = sessionRepository.findByToken(token);

        if(optionalSession.isEmpty()) {
            return false;
        }

        if (!jwtService.validateToken(token)) {
            Session session = optionalSession.get();
            session.setState(State.INACTIVE);
            sessionRepository.save(session);
            return false;
        }

        return true;
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