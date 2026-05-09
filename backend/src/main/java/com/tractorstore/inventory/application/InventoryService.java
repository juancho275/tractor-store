package com.tractorstore.inventory.application;

import com.tractorstore.inventory.application.dto.StockResponse;
import com.tractorstore.inventory.application.dto.StockResponse.StockStatus;
import com.tractorstore.inventory.application.dto.StockUpdateRequest;
import com.tractorstore.inventory.domain.model.Stock;
import com.tractorstore.inventory.domain.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application service for the Inventory bounded context.
 *
 * <p>Manages stock levels per product variant. Provides operations
 * for querying availability, reserving stock for carts, and releasing
 * or deducting after order confirmation.
 *
 * <p>This is the only public API of the Inventory module — other modules
 * (Cart, Order) must use this service, never access repositories directly.
 */
@Service
@Transactional(readOnly = true)
public class InventoryService {

    private final StockRepository stockRepository;

    public InventoryService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Returns stock information for a product variant.
     *
     * @param variantId the variant UUID
     * @return stock response with availability and status
     */
    public StockResponse getStock(UUID variantId) {
        return stockRepository.findByProductVariantId(variantId)
            .map(this::toResponse)
            .orElseThrow(() -> new EntityNotFoundException(
                "Stock not found for variant: " + variantId
            ));
    }

    /**
     * Returns stock for multiple variants in a single call.
     * Used by MFE Explore/Decide to show stock badges on product cards.
     *
     * @param variantIds list of variant UUIDs
     * @return list of stock responses
     */
    public List<StockResponse> getStockBatch(List<UUID> variantIds) {
        return stockRepository.findByProductVariantIdIn(variantIds)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * Reserves stock units for a cart item.
     * Reduces available quantity without deducting from total.
     *
     * @param variantId the variant UUID
     * @param units     number of units to reserve
     * @throws IllegalStateException if insufficient stock
     */
    @Transactional
    public StockResponse reserve(UUID variantId, int units) {
        Stock stock = findOrThrow(variantId);
        stock.reserve(units);
        return toResponse(stockRepository.save(stock));
    }

    /**
     * Releases previously reserved stock.
     * Called when a cart item is removed or cart expires.
     *
     * @param variantId the variant UUID
     * @param units     number of units to release
     */
    @Transactional
    public void release(UUID variantId, int units) {
        stockRepository.findByProductVariantId(variantId)
            .ifPresent(stock -> {
                stock.release(units);
                stockRepository.save(stock);
            });
    }

    /**
     * Deducts stock after a confirmed order.
     * Reduces both total quantity and reservation simultaneously.
     *
     * @param variantId the variant UUID
     * @param units     number of units to deduct
     */
    @Transactional
    public void deduct(UUID variantId, int units) {
        Stock stock = findOrThrow(variantId);
        stock.deduct(units);
        stockRepository.save(stock);
    }

    /**
     * Updates total stock quantity (restocking operation).
     *
     * @param request contains variantId and new quantity
     * @return updated stock response
     */
    @Transactional
    public StockResponse updateStock(StockUpdateRequest request) {
        Stock stock = stockRepository.findByProductVariantId(request.productVariantId())
            .orElseGet(() -> {
                Stock s = new Stock();
                s.setProductVariantId(request.productVariantId());
                return s;
            });
        stock.setQuantity(request.quantity());
        return toResponse(stockRepository.save(stock));
    }

    /**
     * Returns variants with low stock (≤ threshold units available).
     * Used for warehouse alerts and admin dashboard.
     *
     * @param threshold available units threshold (default 5)
     * @return list of low stock responses
     */
    public List<StockResponse> getLowStock(int threshold) {
        return stockRepository.findLowStock(threshold)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private StockResponse toResponse(Stock stock) {
        int available = stock.getAvailable();
        StockStatus status = available == 0 ? StockStatus.OUT_OF_STOCK
            : available <= 5 ? StockStatus.LOW_STOCK
            : StockStatus.IN_STOCK;

        return new StockResponse(
            stock.getProductVariantId(),
            stock.getQuantity(),
            stock.getReserved(),
            available,
            status
        );
    }

    private Stock findOrThrow(UUID variantId) {
        return stockRepository.findByProductVariantId(variantId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Stock not found for variant: " + variantId
            ));
    }
}