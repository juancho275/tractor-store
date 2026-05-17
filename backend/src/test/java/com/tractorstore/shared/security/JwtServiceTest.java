package com.tractorstore.shared.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtService — token generation and validation")
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    // 256-bit base64-encoded key for tests
    private static final String TEST_SECRET =
        "dGhpcy1pcy1hLXNlY3JldC1rZXktdGhhdC1pcy0yNTYtYml0cy1sb25nLXBsZWFzZS1jaGFuZ2U=";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", 86400000L);

        userDetails = new User("test@example.com", "password", List.of());
    }

    @Test
    @DisplayName("generateToken returns a non-blank JWT string")
    void generateToken_returnsNonBlankToken() {
        String token = jwtService.generateToken(userDetails);
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("extractUsername returns the subject set during generation")
    void extractUsername_returnsCorrectEmail() {
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.extractUsername(token)).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("isTokenValid returns true for a freshly generated token")
    void isTokenValid_freshToken_returnsTrue() {
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid returns false when username does not match")
    void isTokenValid_wrongUser_returnsFalse() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = new User("other@example.com", "pass", List.of());
        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    @DisplayName("expired token throws exception on extraction")
    void expiredToken_throwsOnExtract() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String expiredToken = jwtService.generateToken(userDetails);

        assertThatThrownBy(() -> jwtService.extractUsername(expiredToken))
            .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }

    @Test
    @DisplayName("getExpirationMs returns the configured value")
    void getExpirationMs_returnsConfiguredValue() {
        assertThat(jwtService.getExpirationMs()).isEqualTo(86400000L);
    }
}
