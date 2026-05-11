package com.tractorstore.inventory.application;

import com.tractorstore.inventory.application.dto.StockResponse;
import com.tractorstore.inventory.application.dto.StockResponse.StockStatus;
import com.tractorstore.inventory.application.dto.StockUpdateRequest;
import com.tractorstore.inventory.domain.model.Stock;
import com.tractorstore.inventory.domain.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InventoryService.
 *
 * Tests business logic in isolation using Mockito mocks.
 * No Spring context loaded — fast execution.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService")
class InventoryServiceTest {

    @Mock private StockRepository stockRepository;
    @InjectMocks private InventoryService inventoryService;

    private UUID variantId;
    private Stock stock;

    @BeforeEach
    void setUp() {
        variantId = UUID.randomUUID();
        stock = createStock(variantId, 20, 0);
    }

    @Nested
    @DisplayName("getStock()")
    class GetStock {

        @Test
        @DisplayName("returns IN_STOCK when available > 5")
        void returnsInStockWhenAvailableGreaterThanFive() {
            when(stockRepository.findByProductVariantId(variantId))
                .thenReturn(Optional.of(stock));

            StockResponse response = inventoryService.getStock(variantId);

            assertThat(response.status()).isEqualTo(StockStatus.IN_STOCK);
            assertThat(response.available()).isEqualTo(20);
            assertThat(response.productVariantId()).isEqualTo(variantId);
        }

        @Test
        @DisplayName("returns LOW_STOCK when available between 1 and 5")
        void returnsLowStockWhenAvailableBetweenOneAndFive() {
            Stock lowStock = createStock(variantId, 3, 0);
            when(stockRepository.findByProductVariantId(variantId))
                .thenReturn(Optional.of(lowStock));

            StockResponse response = inventoryService.getStock(variantId);

            assertThat(response.status()).isEqualTo(StockStatus.LOW_STOCK);
            assertThat(response.available()).isEqualTo(3);
        }

        @Test
        @DisplayName("returns OUT_OF_STOCK when available is 0")
        void returnsOutOfStockWhenAvailableIsZero() {
            Stock emptyStock = createStock(variantId, 5, 5);
            when(stockRepository.findByProductVariantId(variantId))
                .thenReturn(Optional.of(emptyStock));

            StockResponse response = inventoryService.getStock(variantId);

            assertThat(response.status()).isEqualTo(StockStatus.OUT_OF_STOCK);
            assertThat(response.available()).isZero();
        }

        @Test
        @DisplayName("throws EntityNotFoundException when variant not found")
        void throwsExceptionWhenVariantNotFound() {
            when(stockRepository.findByProductVariantId(variantId))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.getStock(variantId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(variantId.toString());
        }
    }

    @Nested
    @DisplayName("reserve()")
    class Reserve {

        @Test
        @DisplayName("reserves stock when sufficient units available")
        void reservesStockWhenSufficientUnitsAvailable() {
            when(stockRepository.findByProductVariantId(variantId))
                .thenReturn(Optional.of(stock));
            when(stockRepository.save(any(Stock.class))).thenReturn(stock);

            StockResponse response = inventoryService.reserve(variantId, 5);

            verify(stockRepository).save(stock);
            assertThat(stock.getReserved()).isEqualTo(5);
        }

        @Test
        @DisplayName("throws exception when insufficient stock")
        void throwsExceptionWhenInsufficientStock() {
            Stock limitedStock = createStock(variantId, 3, 0);
            when(stockRepository.findByProductVariantId(variantId))
                .thenReturn(Optional.of(limitedStock));

            assertThatThrownBy(() -> inventoryService.reserve(variantId, 5))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
        }
    }

    @Nested
    @DisplayName("deduct()")
    class Deduct {

        @Test
        @DisplayName("deducts quantity and reserved after order")
        void deductsQuantityAndReservedAfterOrder() {
            stock.reserve(10);
            when(stockRepository.findByProductVariantId(variantId))
                .thenReturn(Optional.of(stock));
            when(stockRepository.save(any())).thenReturn(stock);

            inventoryService.deduct(variantId, 10);

            verify(stockRepository).save(stock);
            assertThat(stock.getQuantity()).isEqualTo(10);
        }

        @Test
        @DisplayName("does not go below zero on deduct")
        void doesNotGoBelowZeroOnDeduct() {
            Stock smallStock = createStock(variantId, 2, 0);
            when(stockRepository.findByProductVariantId(variantId))
                .thenReturn(Optional.of(smallStock));
            when(stockRepository.save(any())).thenReturn(smallStock);

            inventoryService.deduct(variantId, 10);

            assertThat(smallStock.getQuantity()).isZero();
        }
    }

    @Nested
    @DisplayName("updateStock()")
    class UpdateStock {

        @Test
        @DisplayName("creates new stock record when variant not found")
        void createsNewStockWhenVariantNotFound() {
            when(stockRepository.findByProductVariantId(variantId))
                .thenReturn(Optional.empty());
            when(stockRepository.save(any(Stock.class))).thenReturn(stock);

            StockUpdateRequest request = new StockUpdateRequest(variantId, 50);
            inventoryService.updateStock(request);

            verify(stockRepository).save(any(Stock.class));
        }

        @Test
        @DisplayName("updates existing stock quantity")
        void updatesExistingStockQuantity() {
            when(stockRepository.findByProductVariantId(variantId))
                .thenReturn(Optional.of(stock));
            when(stockRepository.save(any())).thenReturn(stock);

            StockUpdateRequest request = new StockUpdateRequest(variantId, 100);
            inventoryService.updateStock(request);

            assertThat(stock.getQuantity()).isEqualTo(100);
        }
    }

    @Nested
    @DisplayName("getLowStock()")
    class GetLowStock {

        @Test
        @DisplayName("returns variants below threshold")
        void returnsVariantsBelowThreshold() {
            Stock lowStock = createStock(UUID.randomUUID(), 3, 0);
            when(stockRepository.findLowStock(5)).thenReturn(List.of(lowStock));

            List<StockResponse> result = inventoryService.getLowStock(5);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).status()).isEqualTo(StockStatus.LOW_STOCK);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Stock createStock(UUID variantId, int quantity, int reserved) {
        Stock s = new Stock();
        s.setProductVariantId(variantId);
        s.setQuantity(quantity);
        if (reserved > 0) s.reserve(reserved);
        return s;
    }
}