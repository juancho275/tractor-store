package com.tractorstore.inventory.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stock entity — represents inventory levels for a product variant.
 *
 * <p>Part of the Inventory bounded context. Each product variant
 * has exactly one Stock record. The 'reserved' field tracks units
 * committed in active carts but not yet ordered.
 *
 * <p>Table prefix: inventory_ (module isolation convention)
 */
@Entity
@Table(name = "inventory_stock",
    uniqueConstraints = @UniqueConstraint(columnNames = "product_variant_id"))
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_variant_id", nullable = false, unique = true)
    private UUID productVariantId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int reserved;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business methods
    public int getAvailable() {
        return quantity - reserved;
    }

    public boolean hasStock(int requested) {
        return getAvailable() >= requested;
    }

    public void reserve(int units) {
        if (!hasStock(units)) {
            throw new IllegalStateException(
                "Insufficient stock for variant " + productVariantId +
                ". Available: " + getAvailable() + ", Requested: " + units
            );
        }
        this.reserved += units;
    }

    public void release(int units) {
        this.reserved = Math.max(0, this.reserved - units);
    }

    public void deduct(int units) {
        this.quantity = Math.max(0, this.quantity - units);
        this.reserved = Math.max(0, this.reserved - units);
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getProductVariantId() { return productVariantId; }
    public int getQuantity() { return quantity; }
    public int getReserved() { return reserved; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters (for initialization only)
    public void setProductVariantId(UUID productVariantId) {
        this.productVariantId = productVariantId;
    }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}