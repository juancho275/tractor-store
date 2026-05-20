package com.tractorstore.inventory.infrastructure.web;

import com.tractorstore.inventory.application.InventoryService;
import com.tractorstore.inventory.application.dto.StockResponse;
import com.tractorstore.inventory.application.dto.StockResponse.StockStatus;
import com.tractorstore.shared.BaseControllerTest;
import com.tractorstore.shared.WebMvcSecurityTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcSecurityTest(controllers = InventoryController.class)
class InventoryControllerTest extends BaseControllerTest {

    @MockitoBean InventoryService inventoryService;

    private final UUID variantId = UUID.randomUUID();

    @Test
    void getStock_returns200() throws Exception {
        var stock = new StockResponse(variantId, 20, 0, 20, StockStatus.IN_STOCK);
        when(inventoryService.getStock(variantId)).thenReturn(stock);

        mockMvc.perform(get("/api/inventory/{variantId}", variantId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("IN_STOCK"));
    }

    @Test
    void getLowStock_returns200() throws Exception {
        when(inventoryService.getLowStock(anyInt())).thenReturn(List.of());
        mockMvc.perform(get("/api/inventory/low-stock"))
            .andExpect(status().isOk());
    }
}
