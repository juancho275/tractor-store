package com.tractorstore.order;

import java.util.UUID;

public interface OrderConfirmationApi {
    void processPaymentConfirmation(UUID orderId);
}
