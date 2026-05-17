package com.tractorstore.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "New user registration")
public record RegisterRequest(
    @Schema(example = "user@example.com")
    @NotBlank @Email String email,
    @Schema(example = "mypassword123")
    @NotBlank @Size(min = 8) String password
) {}
