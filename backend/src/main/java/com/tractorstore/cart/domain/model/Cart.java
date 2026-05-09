package com.tractorstore.cart.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Cart entity — represents an active shopping session.
 *
 * <p>A cart belongs to a session (anonymous or authenticated user).
 * It holds CartItems and calculates totals automatically.
 * Carts expire after 24 hours of inactivity.
 *
 * <p>Table prefix: cart_ (module isolation convention)
 */
@Entity
@Table(name = "cart_carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @Column(name = "customer_email")
    private String customerEmail;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> items = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CartStatus status = CartStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public enum CartStatus { ACTIVE, CHECKED_OUT, EXPIRED }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        expiresAt = LocalDateTime.now().plusHours(24);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        expiresAt = LocalDateTime.now().plusHours(24);
    }

    // Business methods
    public BigDecimal getTotal() {
        return items.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getItemCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public void addItem(CartItem item) {
        items.stream()
            .filter(i -> i.getProductVariantId().equals(item.getProductVariantId()))
            .findFirst()
            .ifPresentOrElse(
                existing -> existing.setQuantity(existing.getQuantity() + item.getQuantity()),
                () -> {
                    item.setCart(this);
                    items.add(item);
                }
            );
    }

    public void removeItem(UUID itemId) {
        items.removeIf(i -> i.getId().equals(itemId));
    }

    public void clear() { items.clear(); }

    // Getters
    public UUID getId() { return id; }
    public String getSessionId() { return sessionId; }
    public String getCustomerEmail() { return customerEmail; }
    public List<CartItem> getItems() { return items; }
    public CartStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    // Setters
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setCustomerEmail(String email) { this.customerEmail = email; }
    public void setStatus(CartStatus status) { this.status = status; }
}