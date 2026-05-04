package com.tractorstore.catalog.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Full DTO for product detail view — includes variants.
 * Uses Java 21 Record for immutability.
 */
public record ProductResponse(
    UUID id,
    String name,
    String description,
    UUID categoryId,
    BigDecimal basePrice,
    String imageUrl,
    String tags,
    boolean active,
    List<ProductVariantResponse> variants,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}