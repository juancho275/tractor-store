package com.tractorstore.catalog.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Product category")
public record CategoryResponse(
    @Schema(description = "Category unique identifier") UUID id,
    @Schema(description = "Category display name", example = "Tractores Clásicos") String name,
    @Schema(description = "URL-friendly slug", example = "clasicos") String slug,
    @Schema(description = "Category image URL") String imageUrl,
    @Schema(description = "Parent category UUID, null for top-level") UUID parentId
) {}
