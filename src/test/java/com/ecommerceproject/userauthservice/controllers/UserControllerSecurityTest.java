package com.ecommerceproject.userauthservice.controllers;

import com.ecommerceproject.userauthservice.dtos.UserDto;
import com.ecommerceproject.userauthservice.services.ISessionService;
import com.ecommerceproject.userauthservice.services.ITokenService;
import com.ecommerceproject.userauthservice.services.IUserService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
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
@EnableMethodSecurity
class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private ITokenService tokenService;

    @MockitoBean
    private ISessionService sessionService;


    /*
     * =========================================================
     * PATCH /users/{userId}/roles
     * ADMIN ONLY
     * =========================================================
     */

    @Test
    @WithMockUser(roles = "USER")
    void updateRoles_withUserRole_shouldReturn403()
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
                .andExpect(status().isForbidden());

        verify(userService, never())
                .assignRoles(
                        anyLong(),
                        any()
                );
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRoles_withAdminRole_shouldBeAllowed()
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


    /*
     * =========================================================
     * GET /users/{userId}
     * ADMIN ONLY
     * =========================================================
     */

    @Test
    @WithMockUser(roles = "USER")
    void getUserInfo_withUserRole_shouldReturn403()
            throws Exception {

        mockMvc.perform(
                        get("/users/50")
                )
                .andExpect(status().isForbidden());

        verify(userService, never())
                .getUserInfo(anyLong());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserInfo_withAdminRole_shouldBeAllowed()
            throws Exception {

        UserDto user = new UserDto();

        user.setId(50L);
        user.setUsername("test-user");
        user.setEmail("test@example.com");
        user.setRoles(
                List.of("USER")
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
                                .value("test-user")
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("test@example.com")
                );

        verify(userService)
                .getUserInfo(50L);
    }
}
