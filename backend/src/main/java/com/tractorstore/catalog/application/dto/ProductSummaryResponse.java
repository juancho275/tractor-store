package com.tractorstore.catalog.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Lightweight DTO for product list/grid views.
 * Used in paginated catalog endpoints to avoid loading variants.
 * Uses Java 21 Record for immutability.
 */
public record ProductSummaryResponse(
    UUID id,
    String name,
    String description,
    UUID categoryId,
    BigDecimal basePrice,
    String imageUrl,
    String tags
) {}