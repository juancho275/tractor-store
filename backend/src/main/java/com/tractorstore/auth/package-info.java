/**
 * Auth module — JWT-based authentication and user management.
 *
 * <p>Exposes:
 * <ul>
 *   <li>POST /api/auth/register — create account</li>
 *   <li>POST /api/auth/login    — obtain JWT</li>
 * </ul>
 *
 * <p>Cross-cutting integration: {@link com.tractorstore.shared.security.JwtService}
 * and {@link com.tractorstore.shared.security.JwtAuthenticationFilter} live in
 * {@code shared.security} so the security filter chain can reference them without
 * creating a dependency on this module.
 */
@org.springframework.modulith.ApplicationModule(
    displayName = "Auth",
    allowedDependencies = "shared::security"
)
package com.tractorstore.auth;
