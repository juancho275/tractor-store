package com.tractorstore.order;

import java.util.UUID;

public interface OrderConfirmationApi {
    void confirmOrder(UUID orderId);
}
