package com.tractorstore.notifications.application;

import com.tractorstore.order.application.event.OrderPlaced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Notifications listener for Order domain events.
 *
 * <p>Follows the same Spring Modulith cross-module pattern as the Inventory listener:
 * listens to OrderPlaced AFTER the order transaction commits (Outbox delivery),
 * runs in its own transaction, and is retried automatically on failure.
 *
 * <p>The Notifications module has NO direct dependency on Order's internal classes —
 * only the published event interface (OrderPlaced) is consumed.
 */
@Component
public class OrderNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(OrderNotificationListener.class);
    private final NotificationService notificationService;

    public OrderNotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ApplicationModuleListener
    public void on(OrderPlaced event) {
        log.info("[Notifications] Received OrderPlaced for order: {}", event.orderNumber());
        notificationService.sendOrderConfirmation(event);
        log.info("[Notifications] Confirmation processed for order: {}", event.orderNumber());
    }
}
