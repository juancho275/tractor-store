package com.tractorstore.shared.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter")
class JwtAuthenticationFilterTest {

    @Mock JwtService jwtService;
    @Mock UserDetailsService userDetailsService;
    @InjectMocks JwtAuthenticationFilter filter;

    @BeforeEach
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("delega al chain si no hay header Authorization")
    void delegatesWhenNoAuthHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("delega al chain si el header no empieza con Bearer")
    void delegatesWhenNotBearerToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("autentica el usuario con token JWT válido")
    void authenticatesUser_whenValidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.jwt.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        var userDetails = new User("user@tractorstore.com", "pass", List.of());
        when(jwtService.extractUsername("valid.jwt.token")).thenReturn("user@tractorstore.com");
        when(userDetailsService.loadUserByUsername("user@tractorstore.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid.jwt.token", userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo("user@tractorstore.com");
    }

    @Test
    @DisplayName("retorna 401 y detiene el chain si el token está expirado")
    void returns401_whenTokenExpired() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer expired.jwt.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtService.extractUsername("expired.jwt.token"))
                .thenThrow(mock(ExpiredJwtException.class));

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getContentAsString()).contains("token-expired");
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("retorna 401 y detiene el chain si el token es inválido")
    void returns401_whenTokenMalformed() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer bad.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtService.extractUsername("bad.token"))
                .thenThrow(new JwtException("malformed signature"));

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("invalid-token");
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("no sobreescribe autenticación si el contexto ya tiene una sesión activa")
    void skipsAuthentication_whenContextAlreadyAuthenticated() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.jwt.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // email is extracted but context already has auth → loadUserByUsername never called
        when(jwtService.extractUsername("valid.jwt.token")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(chain).doFilter(request, response);
    }
}
