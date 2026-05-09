package com.tractorstore.order.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
    @Email @NotBlank String customerEmail,
    @NotBlank String customerName,
    @NotBlank String shippingAddress,
    @NotEmpty List<OrderItemRequest> items
) {
    public record OrderItemRequest(
        @NotNull UUID productVariantId,
        @NotBlank String productName,
        @NotBlank String variantName,
        @NotNull BigDecimal unitPrice,
        int quantity
    ) {}
}