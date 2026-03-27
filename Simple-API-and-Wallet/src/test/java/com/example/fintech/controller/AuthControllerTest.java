package com.example.fintech.controller;

import com.example.fintech.dto.LoginRequestDTO;
import com.example.fintech.entity.User;
import com.example.fintech.security.CustomUserDetailsService;
import com.example.fintech.service.JwtService;
import com.example.fintech.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Auth Controller Test Class
 */
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtService jwtService;

    /**
     * Test User Registration Controller
     * @throws Exception
     */
    @Test
    void AuthController_RegisterUser_ReturnApiResponseWrapper() throws Exception {

        // ARRANGE
        User user = new User();
        user.setName("lala");
        user.setEmail("la@la.com");
        user.setConfirmationCode("123456");
        user.setConfirmedAccount(false);

        // ACT
        when(userService.registerUser(any())).thenReturn(new User());

        ResultActions response = mockMvc.perform(post("/api/fintech/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        // ASSERT
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Test User Login Controller
     * @throws Exception
     */
    @Test
    void AuthController_LoginUser_ReturnJwtToken() throws Exception {

        // ARRANGE
        LoginRequestDTO request = new LoginRequestDTO("test@example.com", "password");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "test@example.com",
                "password",
                new ArrayList<>()
        );

        // ACT
        given(authenticationManager.authenticate(any()))
                .willReturn(null); // authenticate returns Authentication, but we don't care here

        given(customUserDetailsService.loadUserByUsername(anyString()))
                .willReturn(userDetails);

        given(jwtService.generateToken(any()))
                .willReturn("mocked-jwt-token");


        ResultActions response = mockMvc.perform(post("/api/fintech/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));

        // ASSERT
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }




}
