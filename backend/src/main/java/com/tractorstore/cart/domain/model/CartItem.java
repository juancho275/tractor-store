package com.tractorstore.cart.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * CartItem entity — represents a product variant added to a cart.
 *
 * <p>Stores a snapshot of the price at the time of adding to cart.
 * Price changes after adding do not affect existing cart items.
 */
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

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

    @Column(name = "image_url")
    private String imageUrl;

    // Business method
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters
    public UUID getId() { return id; }
    public Cart getCart() { return cart; }
    public UUID getProductId() { return productId; }
    public UUID getProductVariantId() { return productVariantId; }
    public String getProductName() { return productName; }
    public String getVariantName() { return variantName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setCart(Cart cart) { this.cart = cart; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public void setProductVariantId(UUID id) { this.productVariantId = id; }
    public void setProductName(String name) { this.productName = name; }
    public void setVariantName(String name) { this.variantName = name; }
    public void setUnitPrice(BigDecimal price) { this.unitPrice = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setImageUrl(String url) { this.imageUrl = url; }
}