package com.tractorstore.auth.application;

import com.tractorstore.auth.domain.model.Role;
import com.tractorstore.auth.domain.model.User;
import com.tractorstore.auth.infrastructure.persistence.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds demo users on first startup.
 * Idempotent — skips if admin@tractorstore.com already exists.
 */
@Component
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${seed.admin.password:admin123}")
    private String adminPassword;

    @Value("${seed.user.password:user123}")
    private String userPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        if (userRepository.existsByEmail("admin@tractorstore.com")) {
            return;
        }
        userRepository.save(new User(
            "admin@tractorstore.com",
            passwordEncoder.encode(adminPassword),
            Role.ADMIN
        ));
        userRepository.save(new User(
            "user@tractorstore.com",
            passwordEncoder.encode(userPassword),
            Role.USER
        ));
        log.info("[Auth] Demo users seeded: admin@tractorstore.com / user@tractorstore.com");
    }
}
