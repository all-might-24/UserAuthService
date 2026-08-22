package com.ecommerceproject.userauthservice.security;

import com.ecommerceproject.userauthservice.services.ITokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final ITokenService tokenService;

    public JWTAuthenticationFilter(ITokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String authHeader = request.getHeader("Authorization");

        String bearer = "Bearer ";

        if(authHeader == null || !authHeader.startsWith(bearer)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(bearer.length());

        boolean isValid = tokenService.validateToken(token);

        if(!isValid) {
            //filterChain.doFilter(request, response);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Long userId = tokenService.getUserId(token);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId,  // principal
                null,      // credentials
                Collections.emptyList() // list of roles
        );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}


/*
    Flow :

                     Request
                       ↓
                    JWTAuthenticationFilter
                       ↓
                    Extract Bearer token
                       ↓
                    Validate JWT
                       ↓
                    Extract userId
                       ↓
                    Create Authentication
                       ↓
                    SecurityContextHolder
                       ↓
                    filterChain.doFilter()
                       ↓
                    Controller

 */