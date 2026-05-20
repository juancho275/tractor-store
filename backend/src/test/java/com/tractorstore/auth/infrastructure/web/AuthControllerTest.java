package com.tractorstore.auth.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.auth.application.AuthService;
import com.tractorstore.auth.application.dto.AuthResponse;
import com.tractorstore.auth.application.dto.LoginRequest;
import com.tractorstore.auth.application.dto.RegisterRequest;
import com.tractorstore.shared.BaseControllerTest;
import com.tractorstore.shared.WebMvcSecurityTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcSecurityTest(controllers = AuthController.class)
@DisplayName("AuthController — login and register endpoints")
class AuthControllerTest extends BaseControllerTest {

    @Autowired ObjectMapper objectMapper;
    @MockitoBean AuthService authService;
    @MockitoBean com.tractorstore.shared.security.JwtService jwtService;

    private static final AuthResponse SAMPLE_RESPONSE =
        new AuthResponse("eyJhbGciOiJIUzI1NiJ9.sample", "admin@tractorstore.com", "ADMIN", 86400000L);

    @Test
    @DisplayName("POST /api/auth/login with valid credentials returns 200 with token")
    void login_validCredentials_returns200() throws Exception {
        when(authService.login(any())).thenReturn(SAMPLE_RESPONSE);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("admin@tractorstore.com", "admin123"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.email").value("admin@tractorstore.com"))
            .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /api/auth/login with wrong password returns 401")
    void login_wrongPassword_returns401() throws Exception {
        when(authService.login(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("admin@tractorstore.com", "wrong"))))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login with invalid body returns 400")
    void login_missingEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"\",\"password\":\"admin123\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register with new email returns 201 with token")
    void register_newEmail_returns201() throws Exception {
        when(authService.register(any())).thenReturn(
            new AuthResponse("eyJtoken", "user@example.com", "USER", 86400000L));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterRequest("user@example.com", "password123"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("POST /api/auth/register with duplicate email returns 400")
    void register_duplicateEmail_returns400() throws Exception {
        when(authService.register(any())).thenThrow(new IllegalArgumentException("Email already registered"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterRequest("dup@example.com", "password123"))))
            .andExpect(status().isBadRequest());
    }
}
