package com.ecommerceproject.userauthservice.security;

import com.ecommerceproject.userauthservice.services.ISessionService;
import com.ecommerceproject.userauthservice.services.ITokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final ITokenService tokenService;

    private final ISessionService sessionService;

    public JWTAuthenticationFilter(ITokenService tokenService, ISessionService sessionService) {
        this.tokenService = tokenService;
        this.sessionService = sessionService;
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

        boolean isTokenValid = tokenService.validateToken(token);
        boolean isSessionValid = sessionService.isSessionValid(token);

        if(!isTokenValid || !isSessionValid) {
            //filterChain.doFilter(request, response);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Long userId = tokenService.getUserId(token);


        List<String> roles = tokenService.getUserRoles(token);

        List<GrantedAuthority> authorities = roles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());


        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId,  // principal
                null,      // credentials
                authorities // list of roles
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
                    Extract userId, roles
                       ↓
                    Create Authentication
                       ↓
                    SecurityContextHolder
                       ↓
                    filterChain.doFilter()
                       ↓
                    Controller

 */