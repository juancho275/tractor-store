package com.tractorstore.cart.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(
    UUID id,
    String sessionId,
    List<CartItemResponse> items,
    int itemCount,
    BigDecimal total,
    String status
) {}