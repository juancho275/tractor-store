package com.tractorstore.inventory.infrastructure.web;

import com.tractorstore.inventory.application.InventoryService;
import com.tractorstore.inventory.application.dto.StockResponse;
import com.tractorstore.inventory.application.dto.StockUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for the Inventory module.
 *
 * <p>Exposes stock availability endpoints consumed by MFE Decide
 * for real-time stock indicators on product pages.
 *
 * <p>Base path: {@code /api/inventory}
 */
@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory", description = "Stock availability and management API")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{variantId}")
    @Operation(summary = "Get stock for a variant")
    public ResponseEntity<StockResponse> getStock(@PathVariable UUID variantId) {
        return ResponseEntity.ok(inventoryService.getStock(variantId));
    }

    @GetMapping("/batch")
    @Operation(summary = "Get stock for multiple variants")
    public ResponseEntity<List<StockResponse>> getStockBatch(
        @RequestParam List<UUID> variantIds
    ) {
        return ResponseEntity.ok(inventoryService.getStockBatch(variantIds));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get variants with low stock")
    public ResponseEntity<List<StockResponse>> getLowStock(
        @RequestParam(defaultValue = "5") int threshold
    ) {
        return ResponseEntity.ok(inventoryService.getLowStock(threshold));
    }

    @PutMapping
    @Operation(summary = "Update stock quantity for a variant")
    public ResponseEntity<StockResponse> updateStock(
        @Valid @RequestBody StockUpdateRequest request
    ) {
        return ResponseEntity.ok(inventoryService.updateStock(request));
    }
}