package com.tractorstore.inventory.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Stock availability for a product variant")
public record StockResponse(
    @Schema(description = "Product variant UUID") UUID productVariantId,
    @Schema(description = "Total units in warehouse") int quantity,
    @Schema(description = "Units reserved for pending orders") int reserved,
    @Schema(description = "Units available for purchase (quantity - reserved)") int available,
    @Schema(description = "Stock status label") StockStatus status
) {
    public enum StockStatus {
        IN_STOCK,      // available > 5
        LOW_STOCK,     // 1 <= available <= 5
        OUT_OF_STOCK   // available == 0
    }
}
