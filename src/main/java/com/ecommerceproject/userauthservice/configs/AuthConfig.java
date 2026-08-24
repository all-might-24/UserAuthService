package com.ecommerceproject.userauthservice.configs;

import com.ecommerceproject.userauthservice.security.JWTAuthenticationFilter;
//import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
//import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableMethodSecurity
public class AuthConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public BCryptPasswordEncoder getBcryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecretKey getSecretKey() {
//        MacAlgorithm macAlgorithm = Jwts.SIG.HS256;
//        return macAlgorithm.key().build();
//    }

    @Bean
    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JWTAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/auth/signup",
                                "/auth/login",
                                "/auth/validate-token"
                                //"/auth/test/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint((request, response, authException)
                                        -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                                .accessDeniedHandler((request, response, accessDeniedException)
                                        -> response.setStatus(HttpServletResponse.SC_FORBIDDEN))
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
