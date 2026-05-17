package com.tractorstore.cart.application;

import com.tractorstore.cart.application.dto.*;
import com.tractorstore.cart.domain.model.Cart;
import com.tractorstore.cart.domain.model.Cart.CartStatus;
import com.tractorstore.cart.domain.model.CartItem;
import com.tractorstore.cart.domain.repository.CartRepository;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Application service for the Cart bounded context.
 *
 * <p>Manages shopping cart lifecycle: creation, item management,
 * total calculation and checkout preparation.
 *
 * <p>Cart isolation: each cart is identified by a sessionId.
 * The Cart module does NOT directly access Catalog or Inventory
 * repositories — it receives prices from the client (MFE Decide)
 * which fetches them from the Catalog API.
 */
@Service
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final MeterRegistry meterRegistry;

    public CartService(CartRepository cartRepository, MeterRegistry meterRegistry) {
        this.cartRepository = cartRepository;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Returns the active cart for a session, creating one if it doesn't exist.
     */
    @Transactional
    public CartResponse getOrCreateCart(String sessionId) {
        Cart cart = cartRepository
            .findBySessionIdAndStatus(sessionId, CartStatus.ACTIVE)
            .orElseGet(() -> createCart(sessionId));
        return toResponse(cart);
    }

    /**
     * Adds an item to the cart. If the variant already exists, increments quantity.
     */
    @Transactional
    public CartResponse addItem(String sessionId, CartItemRequest request) {
        Cart cart = getActiveCart(sessionId);
        CartItem item = new CartItem();
        item.setProductId(request.productId());
        item.setProductVariantId(request.productVariantId());
        item.setProductName(request.productName());
        item.setVariantName(request.variantName());
        item.setUnitPrice(request.unitPrice());
        item.setQuantity(request.quantity());
        item.setImageUrl(request.imageUrl());
        cart.addItem(item);
        CartResponse response = toResponse(cartRepository.save(cart));
        meterRegistry.counter("cart.items.added", "app", "tractor-store-backend").increment();
        return response;
    }

    /**
     * Updates the quantity of an existing cart item.
     * If quantity is 0, removes the item.
     */
    @Transactional
    public CartResponse updateItemQuantity(String sessionId, UUID itemId, int quantity) {
        Cart cart = getActiveCart(sessionId);
        if (quantity <= 0) {
            cart.removeItem(itemId);
        } else {
            cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .ifPresent(i -> i.setQuantity(quantity));
        }
        return toResponse(cartRepository.save(cart));
    }

    /**
     * Removes a specific item from the cart.
     */
    @Transactional
    public CartResponse removeItem(String sessionId, UUID itemId) {
        Cart cart = getActiveCart(sessionId);
        cart.removeItem(itemId);
        return toResponse(cartRepository.save(cart));
    }

    /**
     * Clears all items from the cart.
     */
    @Transactional
    public CartResponse clearCart(String sessionId) {
        Cart cart = getActiveCart(sessionId);
        cart.clear();
        return toResponse(cartRepository.save(cart));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Cart createCart(String sessionId) {
        Cart cart = new Cart();
        cart.setSessionId(sessionId);
        return cartRepository.save(cart);
    }

    private Cart getActiveCart(String sessionId) {
        return cartRepository
            .findBySessionIdAndStatus(sessionId, CartStatus.ACTIVE)
            .orElseThrow(() -> new EntityNotFoundException(
                "Active cart not found for session: " + sessionId
            ));
    }

    private CartResponse toResponse(Cart cart) {
        var items = cart.getItems().stream()
            .map(i -> new CartItemResponse(
                i.getId(), i.getProductId(), i.getProductVariantId(),
                i.getProductName(), i.getVariantName(),
                i.getUnitPrice(), i.getQuantity(), i.getSubtotal(),
                i.getImageUrl()
            ))
            .toList();

        return new CartResponse(
            cart.getId(), cart.getSessionId(), items,
            cart.getItemCount(), cart.getTotal(),
            cart.getStatus().name()
        );
    }
}