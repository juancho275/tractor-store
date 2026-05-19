package com.tractorstore.payment.application;

import com.tractorstore.order.OrderConfirmationApi;
import com.tractorstore.order.application.event.OrderPlaced;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentSimulatorListener")
class PaymentSimulatorListenerTest {

    @Mock OrderConfirmationApi orderConfirmationApi;

    @Test
    @DisplayName("confirms order after processing payment")
    void confirmsOrderAfterPaymentProcessing() throws InterruptedException {
        PaymentSimulatorListener listener = new PaymentSimulatorListener(orderConfirmationApi, 0);
        UUID orderId = UUID.randomUUID();
        OrderPlaced event = new OrderPlaced(
            orderId, "TS-99999-1001", "buyer@test.com",
            new BigDecimal("45000000"), List.of()
        );

        listener.on(event);

        verify(orderConfirmationApi).processPaymentConfirmation(orderId);
    }
}
