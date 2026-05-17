package com.tractorstore.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login credentials")
public record LoginRequest(
    @Schema(example = "admin@tractorstore.com")
    @NotBlank @Email String email,
    @Schema(example = "admin123")
    @NotBlank String password
) {}
