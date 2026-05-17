package com.tractorstore.auth.application;

import com.tractorstore.auth.domain.model.Role;
import com.tractorstore.auth.domain.model.User;
import com.tractorstore.auth.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl — loadUserByUsername")
class UserDetailsServiceImplTest {

    @Mock UserRepository userRepository;
    @InjectMocks UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("existing email returns UserDetails with correct username, password and role")
    void loadUserByUsername_existingEmail_returnsUserDetails() {
        User user = new User("user@example.com", "encoded-pass", Role.USER);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("user@example.com");

        assertThat(userDetails.getUsername()).isEqualTo("user@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("encoded-pass");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("ADMIN role is mapped to ROLE_ADMIN authority")
    void loadUserByUsername_adminRole_returnsAdminAuthority() {
        User user = new User("admin@example.com", "encoded-pass", Role.ADMIN);
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@example.com");

        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("unknown email throws UsernameNotFoundException")
    void loadUserByUsername_notFound_throwsUsernameNotFoundException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown@example.com"))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining("User not found");
    }
}
