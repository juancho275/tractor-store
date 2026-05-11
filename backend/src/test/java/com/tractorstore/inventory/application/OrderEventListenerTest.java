package com.tractorstore.inventory.application;

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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderEventListener")
class OrderEventListenerTest {

    @Mock private InventoryService inventoryService;
    @InjectMocks private OrderEventListener listener;

    @Test
    @DisplayName("deducts stock for each item in the order")
    void deductsStockForEachItem() {
        UUID variant1 = UUID.randomUUID();
        UUID variant2 = UUID.randomUUID();

        OrderPlaced event = new OrderPlaced(
            UUID.randomUUID(), "TS-TEST-1001", "test@test.com",
            new BigDecimal("55000000"),
            List.of(
                new OrderPlaced.OrderPlacedItem(variant1, "Tractor", "Rojo", new BigDecimal("45000000"), 2),
                new OrderPlaced.OrderPlacedItem(variant2, "Arado", "Std", new BigDecimal("10000000"), 1)
            )
        );

        listener.on(event);

        verify(inventoryService).deduct(variant1, 2);
        verify(inventoryService).deduct(variant2, 1);
    }

    @Test
    @DisplayName("propagates exception when deduction fails")
    void propagatesExceptionWhenDeductionFails() {
        UUID variantId = UUID.randomUUID();
        doThrow(new RuntimeException("Stock error"))
            .when(inventoryService).deduct(variantId, 1);

        OrderPlaced event = new OrderPlaced(
            UUID.randomUUID(), "TS-TEST-1002", "test@test.com",
            new BigDecimal("45000000"),
            List.of(new OrderPlaced.OrderPlacedItem(
                variantId, "Tractor", "Rojo", new BigDecimal("45000000"), 1))
        );

        assertThatThrownBy(() -> listener.on(event))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Stock error");
    }
}