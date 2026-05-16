package com.tractorstore.catalog.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Lightweight product view for catalog grid")
public record ProductSummaryResponse(
    @Schema(description = "Product unique identifier") UUID id,
    @Schema(description = "Product name") String name,
    @Schema(description = "Short product description") String description,
    @Schema(description = "Category UUID") UUID categoryId,
    @Schema(description = "Base price in COP", example = "150000000") BigDecimal basePrice,
    @Schema(description = "Product image URL") String imageUrl,
    @Schema(description = "Comma-separated product tags") String tags
) {}
