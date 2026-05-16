package com.tractorstore.notifications.application;

import com.tractorstore.order.application.event.OrderPlaced;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderNotificationListener — routes OrderPlaced to NotificationService")
class OrderNotificationListenerTest {

    @Mock
    NotificationService notificationService;

    @InjectMocks
    OrderNotificationListener listener;

    @Test
    @DisplayName("on(OrderPlaced) delegates to NotificationService.sendOrderConfirmation")
    void on_orderPlaced_callsNotificationService() {
        OrderPlaced event = orderPlacedEvent();

        listener.on(event);

        verify(notificationService, times(1)).sendOrderConfirmation(event);
    }

    private OrderPlaced orderPlacedEvent() {
        return new OrderPlaced(
            UUID.randomUUID(),
            "TS-12345-1001",
            "cliente@example.com",
            new BigDecimal("25000000"),
            List.of(new OrderPlaced.OrderPlacedItem(
                UUID.randomUUID(),
                "Tractor Clásico 4x4",
                "Rojo",
                new BigDecimal("25000000"),
                1
            ))
        );
    }
}
