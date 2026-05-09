package com.tractorstore.order.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    String orderNumber,
    String customerEmail,
    String customerName,
    List<OrderItemResponse> items,
    BigDecimal total,
    String status,
    String shippingAddress,
    LocalDateTime createdAt
) {
    public record OrderItemResponse(
        UUID productVariantId,
        String productName,
        String variantName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal subtotal
    ) {}
}