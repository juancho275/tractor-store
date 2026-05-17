package com.tractorstore.catalog.infrastructure.web;

import com.tractorstore.catalog.application.CatalogService;
import com.tractorstore.catalog.application.dto.CategoryResponse;
import com.tractorstore.catalog.application.dto.ProductResponse;
import com.tractorstore.catalog.application.dto.ProductSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = CatalogController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    }
)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
@DisplayName("CatalogController")
class CatalogControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean CatalogService catalogService;
    @MockitoBean com.tractorstore.shared.security.JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    @MockitoBean io.micrometer.core.instrument.MeterRegistry meterRegistry;

    private final UUID productId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();

    @Test
    @DisplayName("GET /api/catalog/products returns 200")
    void getProducts_returns200() throws Exception {
        var summary = new ProductSummaryResponse(
            productId, "TractorPro X200", "Desc",
            categoryId, new BigDecimal("45000000"), null, null);
        when(catalogService.getProducts(any()))
            .thenReturn(new PageImpl<>(List.of(summary)));

        mockMvc.perform(get("/api/catalog/products"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/catalog/products/{id} returns 200")
    void getProduct_returns200() throws Exception {
        var response = new ProductResponse(
            productId, "TractorPro X200", "Desc",
            categoryId, new BigDecimal("45000000"),
            null, null, true, List.of(), null, null);
        when(catalogService.getProduct(productId)).thenReturn(response);

        mockMvc.perform(get("/api/catalog/products/{id}", productId))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/catalog/categories returns 200")
    void getCategories_returns200() throws Exception {
        var category = new CategoryResponse(
            categoryId, "Tractores", "tractores", null, null);
        when(catalogService.getCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/catalog/categories"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/catalog/categories/{slug} returns 200")
    void getCategoryBySlug_returns200() throws Exception {
        var category = new CategoryResponse(
            categoryId, "Tractores", "tractores", null, null);
        when(catalogService.getCategoryBySlug("tractores")).thenReturn(category);

        mockMvc.perform(get("/api/catalog/categories/{slug}", "tractores"))
            .andExpect(status().isOk());
    }
}