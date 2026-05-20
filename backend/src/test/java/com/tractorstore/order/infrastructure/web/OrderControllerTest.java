package com.tractorstore.order.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractorstore.order.application.OrderService;
import com.tractorstore.order.application.dto.CreateOrderRequest;
import com.tractorstore.order.application.dto.OrderResponse;
import com.tractorstore.shared.BaseControllerTest;
import com.tractorstore.shared.WebMvcSecurityTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcSecurityTest(controllers = OrderController.class)
class OrderControllerTest extends BaseControllerTest {

    @Autowired ObjectMapper objectMapper;
    @MockitoBean OrderService orderService;

    OrderResponse sampleOrder() {
        return new OrderResponse(
            UUID.randomUUID(), "TS-12345-1001", "test@test.com",
            "Test User", List.of(), new BigDecimal("45000000"),
            "PENDING", "Calle 123", null);
    }

    @Test
    void createOrder_returns201() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
            "test@test.com", "Test User", "Calle 123 #45-67",
            List.of(new CreateOrderRequest.OrderItemRequest(
                UUID.randomUUID(), "Tractor", "Rojo",
                new BigDecimal("45000000"), 1)));

        when(orderService.createOrder(any())).thenReturn(sampleOrder());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    void getOrder_returns200() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.getOrder(orderId)).thenReturn(sampleOrder());

        mockMvc.perform(get("/api/orders/{id}", orderId))
            .andExpect(status().isOk());
    }

    @Test
    void getOrdersByEmail_returns200() throws Exception {
        when(orderService.getOrdersByEmail("test@test.com"))
            .thenReturn(List.of(sampleOrder()));

        mockMvc.perform(get("/api/orders/customer/{email}", "test@test.com"))
            .andExpect(status().isOk());
    }

    @Test
    void confirmOrder_returns200() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.confirmOrder(orderId)).thenReturn(sampleOrder());

        mockMvc.perform(put("/api/orders/{id}/confirm", orderId))
            .andExpect(status().isOk());
    }

    @Test
    void cancelOrder_returns200() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.cancelOrder(orderId)).thenReturn(sampleOrder());

        mockMvc.perform(put("/api/orders/{id}/cancel", orderId))
            .andExpect(status().isOk());
    }
}
