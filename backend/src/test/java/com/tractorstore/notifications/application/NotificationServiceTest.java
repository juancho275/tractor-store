package com.tractorstore.notifications.application;

import com.tractorstore.order.application.event.OrderPlaced;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@DisplayName("NotificationService — order confirmation email logic")
class NotificationServiceTest {

    @Test
    @DisplayName("sendOrderConfirmation logs email without throwing when no mailSender configured")
    void sendOrderConfirmation_noMailSender_logsAndDoesNotThrow() {
        NotificationService service = new NotificationService(null);

        OrderPlaced event = new OrderPlaced(
            UUID.randomUUID(),
            "TS-99999-1002",
            "test@example.com",
            new BigDecimal("150000000"),
            List.of(
                new OrderPlaced.OrderPlacedItem(
                    UUID.randomUUID(),
                    "Tractor Autónomo X1",
                    "Verde - GPS incluido",
                    new BigDecimal("150000000"),
                    1
                )
            )
        );

        // Should not throw even without JavaMailSender
        service.sendOrderConfirmation(event);
    }
}
