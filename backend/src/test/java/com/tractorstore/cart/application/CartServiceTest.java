package com.tractorstore.cart.application;

import com.tractorstore.cart.application.dto.CartItemRequest;
import com.tractorstore.cart.application.dto.CartResponse;
import com.tractorstore.cart.domain.model.Cart;
import com.tractorstore.cart.domain.model.Cart.CartStatus;
import com.tractorstore.cart.domain.repository.CartRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService")
class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @InjectMocks private CartService cartService;

    private static final String SESSION_ID = "test-session-123";
    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setSessionId(SESSION_ID);
    }

    @Nested
    @DisplayName("getOrCreateCart()")
    class GetOrCreateCart {

        @Test
        @DisplayName("returns existing active cart when found")
        void returnsExistingCartWhenFound() {
            when(cartRepository.findBySessionIdAndStatus(SESSION_ID, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));

            CartResponse response = cartService.getOrCreateCart(SESSION_ID);

            assertThat(response.sessionId()).isEqualTo(SESSION_ID);
            verify(cartRepository, never()).save(any());
        }

        @Test
        @DisplayName("creates new cart when none exists")
        void createsNewCartWhenNoneExists() {
            when(cartRepository.findBySessionIdAndStatus(SESSION_ID, CartStatus.ACTIVE))
                .thenReturn(Optional.empty());
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            CartResponse response = cartService.getOrCreateCart(SESSION_ID);

            verify(cartRepository).save(any(Cart.class));
            assertThat(response.sessionId()).isEqualTo(SESSION_ID);
        }
    }

    @Nested
    @DisplayName("addItem()")
    class AddItem {

        @Test
        @DisplayName("adds item to existing cart")
        void addsItemToExistingCart() {
            when(cartRepository.findBySessionIdAndStatus(SESSION_ID, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            CartItemRequest request = new CartItemRequest(
                UUID.randomUUID(), UUID.randomUUID(),
                "TractorPro X200", "Rojo",
                new BigDecimal("45000000"), 1, null
            );

            CartResponse response = cartService.addItem(SESSION_ID, request);

            assertThat(cart.getItems()).hasSize(1);
            assertThat(cart.getItems().get(0).getProductName()).isEqualTo("TractorPro X200");
        }

        @Test
        @DisplayName("increments quantity when same variant added twice")
        void incrementsQuantityWhenSameVariantAddedTwice() {
            when(cartRepository.findBySessionIdAndStatus(SESSION_ID, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));
            when(cartRepository.save(any())).thenReturn(cart);

            UUID variantId = UUID.randomUUID();
            CartItemRequest request = new CartItemRequest(
                UUID.randomUUID(), variantId,
                "Tractor", "Verde", new BigDecimal("50000000"), 1, null
            );

            cartService.addItem(SESSION_ID, request);
            cartService.addItem(SESSION_ID, request);

            assertThat(cart.getItems()).hasSize(1);
            assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("throws exception when no active cart exists")
        void throwsExceptionWhenNoActiveCartExists() {
            when(cartRepository.findBySessionIdAndStatus(SESSION_ID, CartStatus.ACTIVE))
                .thenReturn(Optional.empty());

            CartItemRequest request = new CartItemRequest(
                UUID.randomUUID(), UUID.randomUUID(),
                "Tractor", "Rojo", new BigDecimal("45000000"), 1, null
            );

            assertThatThrownBy(() -> cartService.addItem(SESSION_ID, request))
                .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("clearCart()")
    class ClearCart {

        @Test
        @DisplayName("removes all items from cart")
        void removesAllItemsFromCart() {
            when(cartRepository.findBySessionIdAndStatus(SESSION_ID, CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));
            when(cartRepository.save(any())).thenReturn(cart);

            CartResponse response = cartService.clearCart(SESSION_ID);

            assertThat(cart.getItems()).isEmpty();
            assertThat(response.itemCount()).isZero();
        }
    }
}