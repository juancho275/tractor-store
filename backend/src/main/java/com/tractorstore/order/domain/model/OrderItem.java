package com.tractorstore.order.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_variant_id", nullable = false)
    private UUID productVariantId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "variant_name", nullable = false)
    private String variantName;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters
    public UUID getId() { return id; }
    public Order getOrder() { return order; }
    public UUID getProductVariantId() { return productVariantId; }
    public String getProductName() { return productName; }
    public String getVariantName() { return variantName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    // Setters
    public void setOrder(Order o) { this.order = o; }
    public void setProductVariantId(UUID id) { this.productVariantId = id; }
    public void setProductName(String n) { this.productName = n; }
    public void setVariantName(String n) { this.variantName = n; }
    public void setUnitPrice(BigDecimal p) { this.unitPrice = p; }
    public void setQuantity(int q) { this.quantity = q; }
}