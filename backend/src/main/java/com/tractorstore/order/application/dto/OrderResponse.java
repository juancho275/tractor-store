package com.tractorstore.order.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Order details")
public record OrderResponse(
    @Schema(description = "Order unique identifier") UUID id,
    @Schema(description = "Human-readable order number", example = "ORD-20240516-001") String orderNumber,
    @Schema(description = "Customer email") String customerEmail,
    @Schema(description = "Customer full name") String customerName,
    @Schema(description = "Line items") List<OrderItemResponse> items,
    @Schema(description = "Total amount in COP") BigDecimal total,
    @Schema(description = "Order status", example = "PENDING", allowableValues = {"PENDING", "CONFIRMED", "CANCELLED"}) String status,
    @Schema(description = "Shipping address") String shippingAddress,
    @Schema(description = "Order creation timestamp") LocalDateTime createdAt
) {
    @Schema(description = "Single line item within an order")
    public record OrderItemResponse(
        @Schema(description = "Product variant UUID") UUID productVariantId,
        @Schema(description = "Product name at time of purchase") String productName,
        @Schema(description = "Variant name at time of purchase") String variantName,
        @Schema(description = "Unit price in COP") BigDecimal unitPrice,
        @Schema(description = "Quantity ordered") int quantity,
        @Schema(description = "unitPrice × quantity") BigDecimal subtotal
    ) {}
}
