package com.tractorstore.cart.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CartItemRequest(
    @NotNull UUID productId,
    @NotNull UUID productVariantId,
    @NotNull String productName,
    @NotNull String variantName,
    @NotNull BigDecimal unitPrice,
    @Min(1) int quantity,
    String imageUrl
) {}