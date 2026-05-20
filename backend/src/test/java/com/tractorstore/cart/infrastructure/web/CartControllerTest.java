package com.tractorstore.cart.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.cart.application.CartService;
import com.tractorstore.cart.application.dto.CartItemRequest;
import com.tractorstore.cart.application.dto.CartResponse;
import com.tractorstore.shared.BaseControllerTest;
import com.tractorstore.shared.WebMvcSecurityTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcSecurityTest(controllers = CartController.class)
class CartControllerTest extends BaseControllerTest {

    @Autowired ObjectMapper objectMapper;
    @MockitoBean CartService cartService;

    private static final String SESSION = "test-session-123";

    CartResponse emptyCart() {
        return new CartResponse(UUID.randomUUID(), SESSION, List.of(), 0, BigDecimal.ZERO, "ACTIVE");
    }

    @Test
    void getCart_returns200() throws Exception {
        when(cartService.getOrCreateCart(SESSION)).thenReturn(emptyCart());
        mockMvc.perform(get("/api/cart").header("X-Session-Id", SESSION))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value(SESSION));
    }

    @Test
    void addItem_returns200() throws Exception {
        CartItemRequest request = new CartItemRequest(
            UUID.randomUUID(), UUID.randomUUID(),
            "Tractor", "Rojo", new BigDecimal("45000000"), 1, null);
        when(cartService.addItem(eq(SESSION), any())).thenReturn(emptyCart());

        mockMvc.perform(post("/api/cart/items")
                .header("X-Session-Id", SESSION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void clearCart_returns200() throws Exception {
        when(cartService.clearCart(SESSION)).thenReturn(emptyCart());
        mockMvc.perform(delete("/api/cart").header("X-Session-Id", SESSION))
            .andExpect(status().isOk());
    }
}
