package com.tractorstore.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration for The Tractor Store backend.
 *
 * <p>Current phase: Basic configuration allowing public access to
 * documentation and monitoring endpoints. JWT authentication will
 * be implemented in HU-17 (Spring Security JWT).
 *
 * <p>Public endpoints:
 * <ul>
 *   <li>Swagger UI and OpenAPI docs</li>
 *   <li>Spring Actuator health and info</li>
 * </ul>
 *
 * <p>All API endpoints under /api/** will require JWT authentication
 * once HU-17 is implemented.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain.
     * Stateless session — no HTTP session created.
     * CSRF disabled (REST API, not form-based).
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Public — API Documentation
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/api-docs.yaml"
                ).permitAll()
                // Public — Monitoring
                .requestMatchers(
                    "/actuator/**"
                ).permitAll()
                // Public — Auth endpoints (HU-17)
                .requestMatchers("/api/auth/**").permitAll()
                // Everything else requires authentication
                // TODO: HU-17 — enable JWT authentication
                .anyRequest().permitAll()  // ← temporal: cambiar a authenticated() en HU-17
            );

        return http.build();
    }

    /**
     * CORS configuration — allows requests from Angular MFE apps.
     * In production, restrict origins to the actual deployed domains.
     *
     * @return the CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:4200",  // Shell App
            "http://localhost:4201",  // MFE Explore
            "http://localhost:4202",  // MFE Decide
            "http://localhost:4203"   // MFE Checkout
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}