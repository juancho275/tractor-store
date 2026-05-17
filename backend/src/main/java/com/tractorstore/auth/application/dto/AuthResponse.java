package com.tractorstore.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT authentication response")
public record AuthResponse(
    @Schema(description = "Bearer token — include as Authorization: Bearer <token>")
    String token,
    @Schema(description = "Authenticated user email")
    String email,
    @Schema(description = "User role", example = "USER")
    String role,
    @Schema(description = "Token lifetime in milliseconds")
    long expiresIn
) {}
