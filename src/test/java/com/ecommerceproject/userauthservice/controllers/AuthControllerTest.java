package com.ecommerceproject.userauthservice.controllers;

import com.ecommerceproject.userauthservice.dtos.*;
import com.ecommerceproject.userauthservice.services.IAuthService;
import com.ecommerceproject.userauthservice.services.ISessionService;
import com.ecommerceproject.userauthservice.services.ITokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAuthService authService;

    /*
     * JWTAuthenticationFilter dependencies.
     * @WebMvcTest may discover the filter even though
     * filters are disabled for MockMvc.
     */
    @MockitoBean
    private ITokenService tokenService;

    @MockitoBean
    private ISessionService sessionService;


    /*
     * =========================================================
     * SIGNUP
     * =========================================================
     */

    @Test
    void signup_withValidRequest_shouldReturn201() throws Exception {

        SignUpRequestDto request =
                createSignupRequest(
                        "bharat",
                        "bharat@example.com",
                        "password123"
                );

        UserDto response = new UserDto();
        response.setId(1L);
        response.setUsername("bharat");
        response.setEmail("bharat@example.com");

        when(authService.signup(
                "bharat",
                "bharat@example.com",
                "password123"
        )).thenReturn(response);

        mockMvc.perform(
                        post("/auth/signup")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("bharat"))
                .andExpect(jsonPath("$.email")
                        .value("bharat@example.com"));

        verify(authService).signup(
                "bharat",
                "bharat@example.com",
                "password123"
        );
    }

    @Test
    void signup_withBlankUsername_shouldReturn400() throws Exception {

        SignUpRequestDto request =
                createSignupRequest(
                        " ",
                        "bharat@example.com",
                        "password123"
                );

        performSignupAndExpectBadRequest(request);

        verify(authService, never())
                .signup(anyString(), anyString(), anyString());
    }

    @Test
    void signup_withInvalidEmail_shouldReturn400() throws Exception {

        SignUpRequestDto request =
                createSignupRequest(
                        "bharat",
                        "not-an-email",
                        "password123"
                );

        performSignupAndExpectBadRequest(request);

        verify(authService, never())
                .signup(anyString(), anyString(), anyString());
    }

    @Test
    void signup_withBlankEmail_shouldReturn400() throws Exception {

        SignUpRequestDto request =
                createSignupRequest(
                        "bharat",
                        "",
                        "password123"
                );

        performSignupAndExpectBadRequest(request);

        verify(authService, never())
                .signup(anyString(), anyString(), anyString());
    }

    @Test
    void signup_withBlankPassword_shouldReturn400() throws Exception {

        SignUpRequestDto request =
                createSignupRequest(
                        "bharat",
                        "bharat@example.com",
                        ""
                );

        performSignupAndExpectBadRequest(request);

        verify(authService, never())
                .signup(anyString(), anyString(), anyString());
    }

    @Test
    void signup_withPasswordShorterThanFourCharacters_shouldReturn400() throws Exception {

        SignUpRequestDto request =
                createSignupRequest(
                        "bharat",
                        "bharat@example.com",
                        "123"
                );

        performSignupAndExpectBadRequest(request);

        verify(authService, never())
                .signup(anyString(), anyString(), anyString());
    }

    @Test
    void signup_withEmptyJson_shouldReturn400() throws Exception {

        mockMvc.perform(
                        post("/auth/signup")
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(status().isBadRequest());

        verify(authService, never())
                .signup(anyString(), anyString(), anyString());
    }


    /*
     * =========================================================
     * LOGIN
     * =========================================================
     */

    @Test
    void login_withValidCredentials_shouldReturn200AndTokenHeader() throws Exception {

        LoginRequestDto request =
                createLoginRequest(
                        "bharat@example.com",
                        "password123"
                );

        UserDto user = new UserDto();
        user.setId(1L);
        user.setUsername("bharat");
        user.setEmail("bharat@example.com");

        UserTokenDto userTokenDto =
                new UserTokenDto(
                        user,
                        "test-jwt-token"
                );

        when(authService.login(
                "bharat@example.com",
                "password123"
        )).thenReturn(userTokenDto);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        header().string(
                                HttpHeaders.SET_COOKIE,
                                containsString("token=test-jwt-token")
                        )
                )
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("bharat"))
                .andExpect(
                        jsonPath("$.email")
                                .value("bharat@example.com")
                );

        verify(authService).login(
                "bharat@example.com",
                "password123"
        );
    }

    @Test
    void login_withInvalidEmailFormat_shouldReturn400() throws Exception {

        LoginRequestDto request =
                createLoginRequest(
                        "invalid-email",
                        "password123"
                );

        performLoginAndExpectBadRequest(request);

        verify(authService, never())
                .login(anyString(), anyString());
    }

    @Test
    void login_withBlankEmail_shouldReturn400() throws Exception {

        LoginRequestDto request =
                createLoginRequest(
                        "",
                        "password123"
                );

        performLoginAndExpectBadRequest(request);

        verify(authService, never())
                .login(anyString(), anyString());
    }

    @Test
    void login_withBlankPassword_shouldReturn400() throws Exception {

        LoginRequestDto request =
                createLoginRequest(
                        "bharat@example.com",
                        ""
                );

        performLoginAndExpectBadRequest(request);

        verify(authService, never())
                .login(anyString(), anyString());
    }


    /*
     * =========================================================
     * VALIDATE TOKEN
     * =========================================================
     */

    @Test
    void validateToken_whenTokenIsValid_shouldReturn200() throws Exception {

        ValidateTokenDto request = new ValidateTokenDto();

        request.setToken("valid-token");

        when(authService.validateToken("valid-token"))
                .thenReturn(true);

        mockMvc.perform(
                        post("/auth/validate-token")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk());

        verify(authService)
                .validateToken("valid-token");
    }

    @Test
    void validateToken_whenTokenIsInvalid_shouldReturn401() throws Exception {

        ValidateTokenDto request =
                new ValidateTokenDto();

        request.setToken("invalid-token");

        when(authService.validateToken("invalid-token"))
                .thenReturn(false);

        mockMvc.perform(
                        post("/auth/validate-token")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isUnauthorized());

        verify(authService)
                .validateToken("invalid-token");
    }


/*
 * =========================================================
 * LOGOUT
 * =========================================================
 */

    @Test
    void logout_withAuthorizationHeader_shouldReturn204() throws Exception {

        String authHeader =
                "Bearer valid-token";

        mockMvc.perform(
                        post("/auth/logout")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        authHeader
                                )
                )
                .andExpect(status().isNoContent());

        verify(authService)
                .logout(authHeader);
    }

    @Test
    void logout_withoutAuthorizationHeader_shouldReturn400()
            throws Exception {

        mockMvc.perform(
                        post("/auth/logout")
                )
                .andExpect(status().isBadRequest());

        verify(authService, never())
                .logout(anyString());
    }


/*
 * =========================================================
 * HELPERS
 * =========================================================
 */

    private SignUpRequestDto createSignupRequest(
            String username,
            String email,
            String password) {

        SignUpRequestDto request =
                new SignUpRequestDto();

        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);

        return request;
    }

    private LoginRequestDto createLoginRequest(
            String email,
            String password) {

        LoginRequestDto request =
                new LoginRequestDto();

        request.setEmail(email);
        request.setPassword(password);

        return request;
    }

    private void performSignupAndExpectBadRequest(
            SignUpRequestDto request) throws Exception {

        mockMvc.perform(
                        post("/auth/signup")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());
    }

    private void performLoginAndExpectBadRequest(
            LoginRequestDto request) throws Exception {

        mockMvc.perform(
                        post("/auth/login")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());
    }
}