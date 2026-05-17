package com.tractorstore.catalog.application;

import com.tractorstore.catalog.application.dto.*;
import com.tractorstore.catalog.domain.model.Category;
import com.tractorstore.catalog.domain.model.Product;
import com.tractorstore.catalog.domain.repository.CategoryRepository;
import com.tractorstore.catalog.domain.repository.ProductRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * Application service for the Catalog bounded context.
 *
 * <p>Handles all read operations for products and categories.
 * This is the only public API of the Catalog module — other modules
 * must use this service (never access repositories directly).
 *
 * <p>All methods are read-only ({@code @Transactional(readOnly = true)})
 * for performance optimization with Hibernate and connection pooling.
 *
 * @see com.tractorstore.catalog.infrastructure.web.CatalogController
 */
@Service
@Transactional(readOnly = true)
public class CatalogService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MeterRegistry meterRegistry;

    public CatalogService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          MeterRegistry meterRegistry) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Returns a paginated list of active products.
     *
     * @param pageable pagination and sorting parameters
     * @return page of product summaries
     */
    public Page<ProductSummaryResponse> getProducts(Pageable pageable) {
        return productRepository
            .findByActiveTrue(pageable)
            .map(this::toSummary);
    }

    /**
     * Returns active products filtered by category.
     *
     * @param categoryId the category UUID to filter by
     * @param pageable   pagination parameters
     * @return page of product summaries in the given category
     */
    public Page<ProductSummaryResponse> getProductsByCategory(UUID categoryId,
                                                               Pageable pageable) {
        return productRepository
            .findByCategoryIdAndActiveTrue(categoryId, pageable)
            .map(this::toSummary);
    }

    /**
     * Returns a full product detail including all variants.
     *
     * @param id the product UUID
     * @return full product response with variants
     * @throws jakarta.persistence.EntityNotFoundException if product not found
     */
    public ProductResponse getProduct(UUID id) {
        ProductResponse product = productRepository
            .findById(id)
            .map(this::toResponse)
            .orElseThrow(() ->
                new jakarta.persistence.EntityNotFoundException(
                    "Product not found with id: " + id
                )
            );
        meterRegistry.counter("tractor.catalog.products.viewed", "app", "tractor-store-backend").increment();
        return product;
    }

    /**
     * Searches products by name or description.
     *
     * @param query    search term (case-insensitive)
     * @param pageable pagination parameters
     * @return matching products
     */
    public Page<ProductSummaryResponse> searchProducts(String query, Pageable pageable) {
        return productRepository
            .searchByQuery(query, pageable)
            .map(this::toSummary);
    }

    /**
     * Returns all categories.
     *
     * @return list of all categories
     */
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll()
            .stream()
            .map(this::toCategoryResponse)
            .toList();
    }

    /**
     * Returns a single category by its URL slug.
     *
     * @param slug the category slug
     * @return category response
     * @throws jakarta.persistence.EntityNotFoundException if not found
     */
    public CategoryResponse getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
            .map(this::toCategoryResponse)
            .orElseThrow(() ->
                new jakarta.persistence.EntityNotFoundException(
                    "Category not found with slug: " + slug
                )
            );
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private ProductSummaryResponse toSummary(Product p) {
        return new ProductSummaryResponse(
            p.getId(), p.getName(), p.getDescription(),
            p.getCategoryId(), p.getBasePrice(),
            p.getImageUrl(), p.getTags()
        );
    }

    private ProductResponse toResponse(Product p) {
        var variants = p.getVariants().stream()
            .filter(v -> v.isActive())
            .map(v -> new ProductVariantResponse(
                v.getId(), v.getSku(), v.getName(),
                v.getPrice(), v.getImageUrl(),
                v.getAttributes(), v.isActive()
            ))
            .toList();

        return new ProductResponse(
            p.getId(), p.getName(), p.getDescription(),
            p.getCategoryId(), p.getBasePrice(), p.getImageUrl(),
            p.getTags(), p.isActive(), variants,
            p.getCreatedAt(), p.getUpdatedAt()
        );
    }

    private CategoryResponse toCategoryResponse(Category c) {
        return new CategoryResponse(
            c.getId(), c.getName(), c.getSlug(),
            c.getImageUrl(), c.getParentId()
        );
    }
}