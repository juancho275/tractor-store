package com.tractorstore.inventory.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Pure unit tests for Stock domain model business logic.
 * No mocks needed — tests the entity directly.
 */
@DisplayName("Stock (domain model)")
class StockTest {

    private Stock createStock(int quantity, int reserved) {
        Stock stock = new Stock();
        stock.setProductVariantId(UUID.randomUUID());
        stock.setQuantity(quantity);
        if (reserved > 0) stock.reserve(reserved);
        return stock;
    }

    @Nested
    @DisplayName("getAvailable()")
    class GetAvailable {

        @Test
        @DisplayName("returns quantity minus reserved")
        void returnsQuantityMinusReserved() {
            Stock stock = createStock(20, 5);
            assertThat(stock.getAvailable()).isEqualTo(15);
        }

        @Test
        @DisplayName("returns zero when fully reserved")
        void returnsZeroWhenFullyReserved() {
            Stock stock = createStock(10, 10);
            assertThat(stock.getAvailable()).isZero();
        }
    }

    @Nested
    @DisplayName("reserve()")
    class Reserve {

        @Test
        @DisplayName("increases reserved count")
        void increasesReservedCount() {
            Stock stock = createStock(20, 0);
            stock.reserve(5);
            assertThat(stock.getReserved()).isEqualTo(5);
            assertThat(stock.getAvailable()).isEqualTo(15);
        }

        @Test
        @DisplayName("throws when reserving more than available")
        void throwsWhenReservingMoreThanAvailable() {
            Stock stock = createStock(5, 0);
            assertThatThrownBy(() -> stock.reserve(10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
        }
    }

    @Nested
    @DisplayName("deduct()")
    class Deduct {

        @Test
        @DisplayName("reduces quantity and reserved simultaneously")
        void reducesQuantityAndReservedSimultaneously() {
            Stock stock = createStock(20, 10);
            stock.deduct(10);
            assertThat(stock.getQuantity()).isEqualTo(10);
            assertThat(stock.getReserved()).isZero();
        }

        @Test
        @DisplayName("never goes below zero")
        void neverGoesBelowZero() {
            Stock stock = createStock(5, 0);
            stock.deduct(100);
            assertThat(stock.getQuantity()).isZero();
        }
    }

    @Nested
    @DisplayName("hasStock()")
    class HasStock {

        @Test
        @DisplayName("returns true when sufficient available")
        void returnsTrueWhenSufficientAvailable() {
            Stock stock = createStock(20, 5);
            assertThat(stock.hasStock(10)).isTrue();
        }

        @Test
        @DisplayName("returns false when insufficient available")
        void returnsFalseWhenInsufficientAvailable() {
            Stock stock = createStock(5, 3);
            assertThat(stock.hasStock(5)).isFalse();
        }
    }
}