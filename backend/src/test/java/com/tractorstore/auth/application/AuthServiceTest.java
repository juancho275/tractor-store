package com.tractorstore.auth.application;

import com.tractorstore.auth.application.dto.AuthResponse;
import com.tractorstore.auth.application.dto.LoginRequest;
import com.tractorstore.auth.application.dto.RegisterRequest;
import com.tractorstore.auth.domain.model.Role;
import com.tractorstore.auth.domain.model.User;
import com.tractorstore.auth.infrastructure.persistence.UserRepository;
import com.tractorstore.shared.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — login and register")
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock AuthenticationManager authenticationManager;
    @Mock UserDetailsService userDetailsService;
    @InjectMocks AuthService authService;

    private static final String EMAIL = "user@example.com";
    private static final String PASSWORD = "password123";
    private static final String ENCODED = "encoded-password";
    private static final String TOKEN = "jwt.token.value";

    @Test
    @DisplayName("login with valid credentials returns AuthResponse with token")
    void login_validCredentials_returnsAuthResponse() {
        User user = new User(EMAIL, ENCODED, Role.USER);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(EMAIL, ENCODED, List.of());

        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(TOKEN);
        when(jwtService.getExpirationMs()).thenReturn(86400000L);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(new LoginRequest(EMAIL, PASSWORD));

        assertThat(response.token()).isEqualTo(TOKEN);
        assertThat(response.email()).isEqualTo(EMAIL);
        assertThat(response.role()).isEqualTo("USER");
        assertThat(response.expiresIn()).isEqualTo(86400000L);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("register with new email creates user and returns AuthResponse")
    void register_newEmail_createsUserAndReturnsAuthResponse() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(EMAIL, ENCODED, List.of());

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(TOKEN);
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        AuthResponse response = authService.register(new RegisterRequest(EMAIL, PASSWORD));

        assertThat(response.token()).isEqualTo(TOKEN);
        assertThat(response.email()).isEqualTo(EMAIL);
        assertThat(response.role()).isEqualTo("USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register with duplicate email throws IllegalArgumentException")
    void register_duplicateEmail_throwsIllegalArgumentException() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        RegisterRequest req = new RegisterRequest(EMAIL, PASSWORD);
        assertThatThrownBy(() -> authService.register(req))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email already registered");
    }
}
