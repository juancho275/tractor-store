package com.tractorstore.payment.application;

import com.tractorstore.order.OrderConfirmationApi;
import com.tractorstore.order.application.event.OrderPlaced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Simulates an external payment gateway processing an order.
 *
 * <p>In a real system this would be a webhook from Stripe, PayU, etc.
 * Here we introduce a deliberate delay to demonstrate the PENDING → CONFIRMED
 * state transition driven by Spring Modulith's Outbox pattern.
 *
 * <p>Flow: OrderPlaced (published) → Outbox delivers AFTER commit →
 * PaymentSimulatorListener runs async → sleeps 10s → confirmOrder().
 */
@Component
public class PaymentSimulatorListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentSimulatorListener.class);
    static final int DEFAULT_PAYMENT_DELAY_MS = 10_000;

    private final OrderConfirmationApi orderConfirmationApi;
    private final int paymentDelayMs;

    @Autowired
    public PaymentSimulatorListener(OrderConfirmationApi orderConfirmationApi) {
        this(orderConfirmationApi, DEFAULT_PAYMENT_DELAY_MS);
    }

    PaymentSimulatorListener(OrderConfirmationApi orderConfirmationApi, int paymentDelayMs) {
        this.orderConfirmationApi = orderConfirmationApi;
        this.paymentDelayMs = paymentDelayMs;
    }

    @ApplicationModuleListener
    public void on(OrderPlaced event) throws InterruptedException {
        log.info("[payment-sim] Iniciando procesamiento de pago para orden {} ...", event.orderNumber());
        Thread.sleep(paymentDelayMs);
        orderConfirmationApi.processPaymentConfirmation(event.orderId());
        log.info("[payment-sim] Pago aprobado. Orden {} confirmada.", event.orderNumber());
    }
}
