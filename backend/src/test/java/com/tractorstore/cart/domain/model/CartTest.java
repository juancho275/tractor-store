package com.tractorstore.cart.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("Cart (domain model)")
class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setSessionId("test-session");
    }

private CartItem createItem(UUID variantId, BigDecimal price, int qty) {
    CartItem item = new CartItem();
    ReflectionTestUtils.setField(item, "id", UUID.randomUUID());
    item.setProductId(UUID.randomUUID());
    item.setProductVariantId(variantId);
    item.setProductName("Tractor");
    item.setVariantName("Rojo");
    item.setUnitPrice(price);
    item.setQuantity(qty);
    return item;
}

    @Nested @DisplayName("addItem()")
    class AddItem {

        @Test @DisplayName("adds item to empty cart")
        void addsItemToEmptyCart() {
            CartItem item = createItem(UUID.randomUUID(), new BigDecimal("45000000"), 1);
            cart.addItem(item);
            assertThat(cart.getItems()).hasSize(1);
        }

        @Test @DisplayName("merges duplicate variants")
        void mergesDuplicateVariants() {
            UUID variantId = UUID.randomUUID();
            CartItem item1 = createItem(variantId, new BigDecimal("45000000"), 1);
            CartItem item2 = createItem(variantId, new BigDecimal("45000000"), 2);
            cart.addItem(item1);
            cart.addItem(item2);
            assertThat(cart.getItems()).hasSize(1);
            assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(3);
        }

        @Test @DisplayName("keeps separate items for different variants")
        void keepsSeparateItemsForDifferentVariants() {
            cart.addItem(createItem(UUID.randomUUID(), new BigDecimal("45000000"), 1));
            cart.addItem(createItem(UUID.randomUUID(), new BigDecimal("62000000"), 1));
            assertThat(cart.getItems()).hasSize(2);
        }
    }

    @Nested @DisplayName("removeItem()")
    class RemoveItem {

        @Test @DisplayName("removes item by id")
        void removesItemById() {
            CartItem item = createItem(UUID.randomUUID(), new BigDecimal("45000000"), 1);
            cart.addItem(item);
            UUID itemId = cart.getItems().get(0).getId();
            cart.removeItem(itemId);
            assertThat(cart.getItems()).isEmpty();
        }

        @Test @DisplayName("does nothing when item not found")
        void doesNothingWhenItemNotFound() {
            cart.addItem(createItem(UUID.randomUUID(), new BigDecimal("45000000"), 1));
            cart.removeItem(UUID.randomUUID());
            assertThat(cart.getItems()).hasSize(1);
        }
    }

    @Nested @DisplayName("getTotal()")
    class GetTotal {

        @Test @DisplayName("returns sum of all item subtotals")
        void returnsSumOfSubtotals() {
            cart.addItem(createItem(UUID.randomUUID(), new BigDecimal("45000000"), 2));
            cart.addItem(createItem(UUID.randomUUID(), new BigDecimal("10000000"), 1));
            assertThat(cart.getTotal()).isEqualByComparingTo("100000000");
        }

        @Test @DisplayName("returns zero for empty cart")
        void returnsZeroForEmptyCart() {
            assertThat(cart.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested @DisplayName("getItemCount()")
    class GetItemCount {

        @Test @DisplayName("returns total quantity of all items")
        void returnsTotalQuantity() {
            cart.addItem(createItem(UUID.randomUUID(), new BigDecimal("45000000"), 3));
            cart.addItem(createItem(UUID.randomUUID(), new BigDecimal("10000000"), 2));
            assertThat(cart.getItemCount()).isEqualTo(5);
        }
    }

    @Test @DisplayName("clear() removes all items")
    void clearRemovesAllItems() {
        cart.addItem(createItem(UUID.randomUUID(), new BigDecimal("45000000"), 1));
        cart.addItem(createItem(UUID.randomUUID(), new BigDecimal("10000000"), 1));
        cart.clear();
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}