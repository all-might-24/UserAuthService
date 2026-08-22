package com.ecommerceproject.userauthservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class JwtTokenService implements ITokenService {

    private final SecretKey secretKey;

    public JwtTokenService(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String generateToken(Map<String, Object> payload) {

//       MacAlgorithm macAlgorithm = Jwts.SIG.HS256;
//       SecretKey secretKey = macAlgorithm.key().build();

        return Jwts.builder().claims(payload).signWith(secretKey).compact();

    }

    @Override
    public boolean validateToken(String token) {
        try {
            Long expiry = parseToken(token).get("exp", Long.class);

            return expiry != null && expiry > System.currentTimeMillis();

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    @Override
    public List<String> getUserRoles(String token) {
        return parseToken(token).get("scope", List.class);
    }

    private Claims parseToken(String token) {
        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        return jwtParser.parseSignedClaims(token).getPayload();
    }
}

/*
                 JWT
                 Parts-3
                   - Headers
                   - Claims(Payload)
                   - Signature

                * Payload = Claims

                * While you can use any keys, following JWT standards is recommended:

                        iat (Issued At): Timestamp of when the token was created.
                        exp (Expiry): When the token becomes invalid.
                        iss (Issuer): Who created the token (e.g., "scaler").
                        userId: Custom claim for identification.
                        scope: Roles/Permissions assigned to the user.
*/