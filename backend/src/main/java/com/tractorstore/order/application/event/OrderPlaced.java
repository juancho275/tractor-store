package com.tractorstore.order.application.event;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Domain Event — published when an order is successfully placed.
 *
 * <p>This event is published via Spring Modulith's Outbox pattern.
 * It is ONLY published after the transaction commits successfully,
 * guaranteeing eventual consistency without distributed transactions.
 *
 * <p>Listeners (Inventory, Notifications) react to this event
 * via @TransactionalEventListener(phase = AFTER_COMMIT).
 *
 * <p>Uses Java 21 Record for immutability — events are value objects.
 */
public record OrderPlaced(
    UUID orderId,
    String orderNumber,
    String customerEmail,
    BigDecimal total,
    List<OrderPlacedItem> items
) {
    /**
     * Snapshot of each item at order time.
     * Prices are captured at checkout — immune to future catalog changes.
     */
    public record OrderPlacedItem(
        UUID productVariantId,
        String productName,
        String variantName,
        BigDecimal unitPrice,
        int quantity
    ) {}
}