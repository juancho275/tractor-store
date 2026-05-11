package com.tractorstore.order.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Order (domain model)")
class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setOrderNumber("TS-TEST-001");
        order.setCustomerEmail("test@test.com");
        order.setTotal(BigDecimal.valueOf(45000000));
        order.setItems(List.of());
    }

    @Test @DisplayName("new order starts PENDING")
    void newOrderStartsPending() {
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
    }

    @Test @DisplayName("confirm() transitions to CONFIRMED")
    void confirmTransitionsToConfirmed() {
        order.confirm();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.CONFIRMED);
    }

    @Test @DisplayName("ship() after confirm transitions to SHIPPED")
    void shipAfterConfirmTransitionsToShipped() {
        order.confirm();
        order.ship();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.SHIPPED);
    }

    @Test @DisplayName("cancel() on PENDING transitions to CANCELLED")
    void cancelPendingOrder() {
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.CANCELLED);
    }

    @Test @DisplayName("confirm() on non-PENDING throws exception")
    void confirmNonPendingThrows() {
        order.confirm();
        assertThatThrownBy(order::confirm)
            .isInstanceOf(IllegalStateException.class);
    }

    @Test @DisplayName("ship() on non-CONFIRMED throws exception")
    void shipNonConfirmedThrows() {
        assertThatThrownBy(order::ship)
            .isInstanceOf(IllegalStateException.class);
    }

    @Test @DisplayName("cancel() on SHIPPED throws exception")
    void cancelShippedThrows() {
        order.confirm();
        order.ship();
        assertThatThrownBy(order::cancel)
            .isInstanceOf(IllegalStateException.class);
    }
}