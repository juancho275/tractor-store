package com.tractorstore.inventory.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Request DTO for updating stock levels.
 * Used by warehouse/admin operations to restock variants.
 */
public record StockUpdateRequest(
    @NotNull UUID productVariantId,
    @Min(0) int quantity
) {}