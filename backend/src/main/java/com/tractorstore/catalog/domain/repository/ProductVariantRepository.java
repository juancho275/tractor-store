package com.tractorstore.catalog.domain.repository;

import com.tractorstore.catalog.domain.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ProductVariant.
 * Part of the Catalog bounded context.
 */
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    /** Find all active variants for a product */
    List<ProductVariant> findByProductIdAndActiveTrue(UUID productId);

    /** Find a variant by its unique SKU */
    Optional<ProductVariant> findBySku(String sku);
}