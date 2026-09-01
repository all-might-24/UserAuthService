package com.ecommerceproject.userauthservice.security;

import com.ecommerceproject.userauthservice.services.ISessionService;
import com.ecommerceproject.userauthservice.services.ITokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTAuthenticationFilterTest {

    @Mock
    private ITokenService tokenService;

    @Mock
    private ISessionService sessionService;

    @Mock
    private FilterChain filterChain;

    private JWTAuthenticationFilter jwtAuthenticationFilter;


    @BeforeEach
    void setUp() {

        jwtAuthenticationFilter =
                new JWTAuthenticationFilter(
                        tokenService,
                        sessionService
                );

        SecurityContextHolder.clearContext();
    }


    @AfterEach
    void tearDown() {

        SecurityContextHolder.clearContext();
    }


    /*
     * =========================================================
     * MISSING AUTHORIZATION HEADER
     * =========================================================
     */

    @Test
    void doFilter_withoutAuthorizationHeader_shouldContinueFilterChain()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(
                tokenService,
                sessionService
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }


    /*
     * =========================================================
     * NON-BEARER HEADER
     * =========================================================
     */

    @Test
    void doFilter_withNonBearerHeader_shouldContinueFilterChain()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Basic abc123"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(
                tokenService,
                sessionService
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }


    /*
     * =========================================================
     * INVALID JWT
     * =========================================================
     */

    @Test
    void doFilter_withInvalidToken_shouldReturn401()
            throws Exception {

        String token = "invalid-token";

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(tokenService.validateToken(token))
                .thenReturn(false);

        when(sessionService.isSessionValid(token))
                .thenReturn(true);

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertEquals(
                HttpServletResponse.SC_UNAUTHORIZED,
                response.getStatus()
        );

        verify(tokenService)
                .validateToken(token);

        verify(sessionService)
                .isSessionValid(token);

        verify(filterChain, never())
                .doFilter(request, response);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }


    /*
     * =========================================================
     * INACTIVE SESSION
     * =========================================================
     */

    @Test
    void doFilter_withInactiveSession_shouldReturn401()
            throws Exception {

        String token = "valid-token";

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(tokenService.validateToken(token))
                .thenReturn(true);

        when(sessionService.isSessionValid(token))
                .thenReturn(false);

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertEquals(
                HttpServletResponse.SC_UNAUTHORIZED,
                response.getStatus()
        );

        verify(tokenService)
                .validateToken(token);

        verify(sessionService)
                .isSessionValid(token);

        verify(filterChain, never())
                .doFilter(request, response);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }


    /*
     * =========================================================
     * VALID TOKEN + ACTIVE SESSION
     * =========================================================
     */

    @Test
    void doFilter_withValidTokenAndActiveSession_shouldAuthenticateUser()
            throws Exception {

        String token = "valid-token";

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(tokenService.validateToken(token))
                .thenReturn(true);

        when(sessionService.isSessionValid(token))
                .thenReturn(true);

        when(tokenService.getUserId(token))
                .thenReturn(10L);

        when(tokenService.getUserRoles(token))
                .thenReturn(
                        List.of("USER")
                );

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(authentication);

        assertEquals(
                10L,
                authentication.getPrincipal()
        );

        assertTrue(
                authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority
                                                .getAuthority()
                                                .equals("ROLE_USER")
                        )
        );

        verify(filterChain)
                .doFilter(request, response);
    }


    /*
     * =========================================================
     * MULTIPLE ROLES
     * =========================================================
     */

    @Test
    void doFilter_withMultipleRoles_shouldCreateCorrectAuthorities()
            throws Exception {

        String token = "admin-token";

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(tokenService.validateToken(token))
                .thenReturn(true);

        when(sessionService.isSessionValid(token))
                .thenReturn(true);

        when(tokenService.getUserId(token))
                .thenReturn(20L);

        when(tokenService.getUserRoles(token))
                .thenReturn(
                        List.of(
                                "USER",
                                "ADMIN"
                        )
                );

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(authentication);

        assertEquals(
                20L,
                authentication.getPrincipal()
        );

        assertEquals(
                2,
                authentication
                        .getAuthorities()
                        .size()
        );

        assertTrue(
                authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority
                                                .getAuthority()
                                                .equals("ROLE_USER")
                        )
        );

        assertTrue(
                authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority
                                                .getAuthority()
                                                .equals("ROLE_ADMIN")
                        )
        );

        verify(filterChain)
                .doFilter(request, response);
    }


    /*
     * =========================================================
     * VERIFY TOKEN EXTRACTION
     * =========================================================
     */

    @Test
    void doFilter_shouldRemoveBearerPrefixBeforeCallingServices()
            throws Exception {

        String token = "actual-jwt-token";

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(tokenService.validateToken(token))
                .thenReturn(true);

        when(sessionService.isSessionValid(token))
                .thenReturn(true);

        when(tokenService.getUserId(token))
                .thenReturn(1L);

        when(tokenService.getUserRoles(token))
                .thenReturn(
                        List.of("USER")
                );

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        verify(tokenService)
                .validateToken("actual-jwt-token");

        verify(sessionService)
                .isSessionValid("actual-jwt-token");

        verify(tokenService)
                .getUserId("actual-jwt-token");

        verify(tokenService)
                .getUserRoles("actual-jwt-token");
    }
}