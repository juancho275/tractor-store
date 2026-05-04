package com.tractorstore.catalog.application.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for product variant responses.
 * Uses Java 21 Record for immutability and conciseness.
 */
public record ProductVariantResponse(
    UUID id,
    String sku,
    String name,
    BigDecimal price,
    String imageUrl,
    Map<String, String> attributes,
    boolean active
) {}