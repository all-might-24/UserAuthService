package com.ecommerceproject.userauthservice.services;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    private SecretKey secretKey;


    @BeforeEach
    void setUp() {

        String secret =
                "this-is-a-test-secret-key-that-is-long-enough-for-hs256";

        secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        jwtTokenService =
                new JwtTokenService(secretKey);
    }


    /*
     * =========================================================
     * GENERATE TOKEN
     * =========================================================
     */

    @Test
    void generateToken_shouldReturnNonEmptyToken() {

        Map<String, Object> payload =
                Map.of(
                        "userId", 1L,
                        "scope", List.of("USER"),
                        "iss", "Issuer"
                );

        String token =
                jwtTokenService.generateToken(payload);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }


    @Test
    void generateToken_shouldGenerateValidToken() {

        Map<String, Object> payload =
                Map.of(
                        "userId", 1L,
                        "scope", List.of("USER")
                );

        String token =
                jwtTokenService.generateToken(payload);

        assertTrue(
                jwtTokenService.validateToken(token)
        );
    }


    /*
     * =========================================================
     * VALIDATE TOKEN
     * =========================================================
     */

    @Test
    void validateToken_shouldReturnTrue_whenTokenIsValid() {

        String token =
                jwtTokenService.generateToken(
                        Map.of(
                                "userId", 1L,
                                "scope", List.of("USER")
                        )
                );

        boolean result =
                jwtTokenService.validateToken(token);

        assertTrue(result);
    }


    @Test
    void validateToken_shouldReturnFalse_whenTokenIsMalformed() {

        String malformedToken =
                "this-is-not-a-valid-jwt";

        boolean result =
                jwtTokenService.validateToken(
                        malformedToken
                );

        assertFalse(result);
    }


    @Test
    void validateToken_shouldReturnFalse_whenTokenIsSignedWithDifferentKey() {

        SecretKey differentKey =
                Keys.hmacShaKeyFor(
                        "this-is-a-completely-different-test-secret-key-for-hs256"
                                .getBytes(StandardCharsets.UTF_8)
                );

        JwtTokenService differentJwtService =
                new JwtTokenService(differentKey);

        String token =
                differentJwtService.generateToken(
                        Map.of(
                                "userId", 1L,
                                "scope", List.of("USER")
                        )
                );

        boolean result =
                jwtTokenService.validateToken(token);

        assertFalse(result);
    }


    /*
     * =========================================================
     * USER ID
     * =========================================================
     */

    @Test
    void getUserId_shouldReturnUserIdFromToken() {

        String token =
                jwtTokenService.generateToken(
                        Map.of(
                                "userId", 25L,
                                "scope", List.of("USER")
                        )
                );

        Long userId =
                jwtTokenService.getUserId(token);

        assertEquals(
                25L,
                userId
        );
    }


    /*
     * =========================================================
     * ROLES
     * =========================================================
     */

    @Test
    void getUserRoles_shouldReturnRolesFromToken() {

        String token =
                jwtTokenService.generateToken(
                        Map.of(
                                "userId", 1L,
                                "scope",
                                List.of(
                                        "USER",
                                        "ADMIN"
                                )
                        )
                );

        List<String> roles =
                jwtTokenService.getUserRoles(token);

        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("USER"));
        assertTrue(roles.contains("ADMIN"));
    }


    @Test
    void getUserRoles_shouldReturnSingleRoleFromToken() {

        String token =
                jwtTokenService.generateToken(
                        Map.of(
                                "userId", 1L,
                                "scope",
                                List.of("USER")
                        )
                );

        List<String> roles =
                jwtTokenService.getUserRoles(token);

        assertEquals(
                List.of("USER"),
                roles
        );
    }
}