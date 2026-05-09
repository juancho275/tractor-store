package com.tractorstore.cart.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
    UUID id,
    UUID productId,
    UUID productVariantId,
    String productName,
    String variantName,
    BigDecimal unitPrice,
    int quantity,
    BigDecimal subtotal,
    String imageUrl
) {}