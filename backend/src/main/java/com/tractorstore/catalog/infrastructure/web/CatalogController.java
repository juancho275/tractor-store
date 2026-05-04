package com.tractorstore.catalog.infrastructure.web;

import com.tractorstore.catalog.application.CatalogService;
import com.tractorstore.catalog.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for the Catalog module.
 *
 * <p>Exposes product and category endpoints consumed by the MFE Explore
 * and MFE Decide micro-frontends. All endpoints are read-only (GET).
 *
 * <p>Base path: {@code /api/catalog}
 *
 * @see CatalogService
 */
@RestController
@RequestMapping("/api/catalog")
@Tag(name = "Catalog", description = "Product catalog and categories API")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * Returns a paginated list of active products.
     *
     * @param pageable  pagination (default: page=0, size=12, sort=name)
     * @param category  optional category UUID filter
     * @param q         optional search query
     * @return paginated product summaries
     */
    @GetMapping("/products")
    @Operation(summary = "List products", description = "Returns paginated active products, optionally filtered by category or search query")
    public ResponseEntity<Page<ProductSummaryResponse>> getProducts(
        @PageableDefault(size = 12, sort = "name") Pageable pageable,
        @RequestParam(required = false) UUID category,
        @RequestParam(required = false) String q
    ) {
        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(catalogService.searchProducts(q, pageable));
        }
        if (category != null) {
            return ResponseEntity.ok(catalogService.getProductsByCategory(category, pageable));
        }
        return ResponseEntity.ok(catalogService.getProducts(pageable));
    }

    /**
     * Returns full product detail including variants.
     *
     * @param id the product UUID
     * @return product with all active variants
     */
    @GetMapping("/products/{id}")
    @Operation(summary = "Get product detail", description = "Returns a single product with all its variants")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogService.getProduct(id));
    }

    /**
     * Returns all categories.
     *
     * @return list of all categories
     */
    @GetMapping("/categories")
    @Operation(summary = "List categories", description = "Returns all product categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(catalogService.getCategories());
    }

    /**
     * Returns a category by its slug.
     *
     * @param slug URL-friendly category identifier
     * @return the matching category
     */
    @GetMapping("/categories/{slug}")
    @Operation(summary = "Get category by slug", description = "Returns a single category by its URL slug")
    public ResponseEntity<CategoryResponse> getCategoryBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(catalogService.getCategoryBySlug(slug));
    }
}