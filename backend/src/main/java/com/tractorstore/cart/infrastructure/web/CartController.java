package com.tractorstore.cart.infrastructure.web;

import com.tractorstore.cart.application.CartService;
import com.tractorstore.cart.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

/**
 * REST controller for the Cart module.
 * Base path: /api/cart
 * Session identified via X-Session-Id header.
 */
@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Shopping cart management API")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Get or create active cart for session")
    public ResponseEntity<CartResponse> getCart(
        @RequestHeader("X-Session-Id") String sessionId
    ) {
        return ResponseEntity.ok(cartService.getOrCreateCart(sessionId));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartResponse> addItem(
        @RequestHeader("X-Session-Id") String sessionId,
        @Valid @RequestBody CartItemRequest request
    ) {
        return ResponseEntity.ok(cartService.addItem(sessionId, request));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update item quantity")
    public ResponseEntity<CartResponse> updateItem(
        @RequestHeader("X-Session-Id") String sessionId,
        @PathVariable UUID itemId,
        @RequestParam int quantity
    ) {
        return ResponseEntity.ok(cartService.updateItemQuantity(sessionId, itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<CartResponse> removeItem(
        @RequestHeader("X-Session-Id") String sessionId,
        @PathVariable UUID itemId
    ) {
        return ResponseEntity.ok(cartService.removeItem(sessionId, itemId));
    }

    @DeleteMapping
    @Operation(summary = "Clear all items from cart")
    public ResponseEntity<CartResponse> clearCart(
        @RequestHeader("X-Session-Id") String sessionId
    ) {
        return ResponseEntity.ok(cartService.clearCart(sessionId));
    }
}