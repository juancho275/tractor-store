package com.tractorstore.auth.application;

import com.tractorstore.auth.domain.model.User;
import com.tractorstore.auth.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataInitializer")
class DataInitializerTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks DataInitializer dataInitializer;

    @Test
    @DisplayName("no crea usuarios si admin ya existe")
    void skipsSeeding_whenAdminAlreadyExists() {
        when(userRepository.existsByEmail("admin@tractorstore.com")).thenReturn(true);

        dataInitializer.seed();

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("crea admin y user en primer arranque")
    void seedsAdminAndUser_whenAdminDoesNotExist() {
        ReflectionTestUtils.setField(dataInitializer, "adminPassword", "admin123");
        ReflectionTestUtils.setField(dataInitializer, "userPassword", "user123");
        when(userRepository.existsByEmail("admin@tractorstore.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

        dataInitializer.seed();

        verify(userRepository, times(2)).save(any(User.class));
    }
}
