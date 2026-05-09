package com.tractorstore.inventory.application.dto;

import java.util.UUID;

/**
 * DTO for stock level responses.
 * Exposes available stock without revealing internal reservation details.
 * Uses Java 21 Record for immutability.
 */
public record StockResponse(
    UUID productVariantId,
    int quantity,
    int reserved,
    int available,
    StockStatus status
) {
    public enum StockStatus {
        IN_STOCK,      // available > 5
        LOW_STOCK,     // 1 <= available <= 5
        OUT_OF_STOCK   // available == 0
    }
}