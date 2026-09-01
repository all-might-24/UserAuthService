package com.ecommerceproject.userauthservice.controllers;

import com.ecommerceproject.userauthservice.services.ISessionService;
import com.ecommerceproject.userauthservice.services.ITokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ISessionService sessionService;

    /*
     * JWTAuthenticationFilter dependency.
     * Filters are disabled because this is a controller-behavior test,
     * not a security test.
     */
    @MockitoBean
    private ITokenService tokenService;


    @Test
    void validateSession_withValidBearerTokenAndActiveSession_shouldReturn200()
            throws Exception {

        String token = "valid-token";

        when(sessionService.isSessionValid(token))
                .thenReturn(true);

        mockMvc.perform(
                        get("/session/validate")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());

        verify(sessionService)
                .isSessionValid(token);
    }


    @Test
    void validateSession_withInactiveSession_shouldReturn401()
            throws Exception {

        String token = "inactive-token";

        when(sessionService.isSessionValid(token))
                .thenReturn(false);

        mockMvc.perform(
                        get("/session/validate")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isUnauthorized());

        verify(sessionService)
                .isSessionValid(token);
    }


    @Test
    void validateSession_withoutAuthorizationHeader_shouldReturn400()
            throws Exception {

        mockMvc.perform(
                        get("/session/validate")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(sessionService);
    }


    @Test
    void validateSession_withNonBearerAuthorizationHeader_shouldReturn401()
            throws Exception {

        mockMvc.perform(
                        get("/session/validate")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Basic abc123"
                                )
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(sessionService);
    }


    @Test
    void validateSession_withEmptyBearerToken_shouldReturn401()
            throws Exception {

        when(sessionService.isSessionValid(""))
                .thenReturn(false);

        mockMvc.perform(
                        get("/session/validate")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer "
                                )
                )
                .andExpect(status().isUnauthorized());

        verify(sessionService)
                .isSessionValid("");
    }
}