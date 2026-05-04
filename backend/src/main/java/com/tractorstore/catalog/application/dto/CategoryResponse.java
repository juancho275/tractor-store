package com.tractorstore.catalog.application.dto;

import java.util.UUID;

/**
 * DTO for category responses.
 * Uses Java 21 Record for immutability.
 */
public record CategoryResponse(
    UUID id,
    String name,
    String slug,
    String imageUrl,
    UUID parentId
) {}