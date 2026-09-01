package com.ecommerceproject.userauthservice.controllers;

import com.ecommerceproject.userauthservice.services.ISessionService;
import com.ecommerceproject.userauthservice.services.ITokenService;
import com.ecommerceproject.userauthservice.services.IUserService;
import com.ecommerceproject.userauthservice.dtos.UserDto;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService userService;

    /*
     * JWTAuthenticationFilter is discovered by the MVC slice.
     *
     * Even though filters are disabled for this controller-only test,
     * Spring may still need these dependencies while creating the
     * application context.
     */
    @MockitoBean
    private ITokenService tokenService;

    @MockitoBean
    private ISessionService sessionService;


    /*
     * =========================================================
     * UPDATE USER ROLES
     * =========================================================
     */

    @Test
    void updateRoles_withValidRequest_shouldReturn204()
            throws Exception {

        mockMvc.perform(
                        patch("/users/10/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "roleNames": [
                                            "USER",
                                            "ADMIN"
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isNoContent());

        verify(userService)
                .assignRoles(
                        10L,
                        List.of(
                                "USER",
                                "ADMIN"
                        )
                );
    }


    @Test
    void updateRoles_withInvalidUserIdType_shouldReturn400()
            throws Exception {

        mockMvc.perform(
                        patch("/users/abc/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "roleNames": [
                                            "USER"
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .assignRoles(
                        anyLong(),
                        any()
                );
    }


    /*
     * =========================================================
     * GET CURRENT USER PROFILE
     * =========================================================
     */

    @Test
    void getMyProfile_shouldReturnCurrentUser()
            throws Exception {

        UsernamePasswordAuthenticationToken authentication =
                createAuthentication(25L);

        UserDto user = new UserDto();

        user.setId(25L);
        user.setUsername("bharat");
        user.setEmail("bharat@example.com");
        user.setRoles(
                List.of("USER")
        );

        when(userService.getUserInfo(25L))
                .thenReturn(user);

        mockMvc.perform(
                        get("/users/me")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(25)
                )
                .andExpect(
                        jsonPath("$.username")
                                .value("bharat")
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("bharat@example.com")
                )
                .andExpect(
                        jsonPath("$.roles[0]")
                                .value("USER")
                );

        verify(userService)
                .getUserInfo(25L);
    }


    /*
     * =========================================================
     * GET CURRENT USER ROLES
     * =========================================================
     */

    @Test
    void getMyProfileRoles_shouldReturnCurrentUserRoles()
            throws Exception {

        UsernamePasswordAuthenticationToken authentication =
                createAuthentication(25L);

        when(userService.getMyProfileRoles(25L))
                .thenReturn(
                        List.of(
                                "USER",
                                "ADMIN"
                        )
                );

        mockMvc.perform(
                        get("/users/me/roles")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0]")
                                .value("USER")
                )
                .andExpect(
                        jsonPath("$[1]")
                                .value("ADMIN")
                );

        verify(userService)
                .getMyProfileRoles(25L);
    }


    /*
     * =========================================================
     * GET USER BY ID
     * =========================================================
     */

    @Test
    void getUserInfo_withValidUserId_shouldReturnUser()
            throws Exception {

        UserDto user = new UserDto();

        user.setId(50L);
        user.setUsername("admin");
        user.setEmail("admin@example.com");
        user.setRoles(
                List.of("ADMIN")
        );

        when(userService.getUserInfo(50L))
                .thenReturn(user);

        mockMvc.perform(
                        get("/users/50")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(50)
                )
                .andExpect(
                        jsonPath("$.username")
                                .value("admin")
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("admin@example.com")
                )
                .andExpect(
                        jsonPath("$.roles[0]")
                                .value("ADMIN")
                );

        verify(userService)
                .getUserInfo(50L);
    }


    @Test
    void getUserInfo_withInvalidUserIdType_shouldReturn400()
            throws Exception {

        mockMvc.perform(
                        get("/users/abc")
                )
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .getUserInfo(anyLong());
    }


    /*
     * =========================================================
     * HELPER
     * =========================================================
     */

    private UsernamePasswordAuthenticationToken createAuthentication(
            Long userId
    ) {

        return new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of()
        );
    }
}