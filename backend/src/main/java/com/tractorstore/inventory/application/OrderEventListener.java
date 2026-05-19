package com.tractorstore.inventory.application;

import com.tractorstore.order.application.event.OrderPlaced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Inventory listener for Order domain events.
 *
 * <p>This is the Spring Modulith cross-module communication pattern:
 * The Inventory module listens to events published by the Order module
 * WITHOUT a direct dependency on Order's internal classes.
 *
 * <p>@ApplicationModuleListener is a Spring Modulith annotation that:
 * 1. Marks this as a cross-module event listener
 * 2. Runs AFTER the publishing transaction commits (Outbox delivery)
 * 3. Runs in its OWN transaction — failure here doesn't roll back the order
 * 4. Spring Modulith retries failed listeners automatically
 */
@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final InventoryService inventoryService;

    public OrderEventListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Deducts stock for each item when an order is placed.
     *
     * <p>Called AFTER the order transaction commits.
     * If this fails, Spring Modulith retries automatically
     * using the event_publication table as the retry store.
     *
     * @param event the OrderPlaced domain event
     */
    @ApplicationModuleListener
    public void on(OrderPlaced event) {
        log.info("Deducting stock for order: {}", event.orderNumber());

        event.items().forEach(item -> {
            inventoryService.deduct(item.productVariantId(), item.quantity());
            log.debug("Stock deducted: variant={} qty={}", item.productVariantId(), item.quantity());
        });

        log.info("Stock deduction complete for order: {}", event.orderNumber());
    }
}