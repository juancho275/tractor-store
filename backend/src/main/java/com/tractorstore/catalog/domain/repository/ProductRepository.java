package com.tractorstore.catalog.domain.repository;

import com.tractorstore.catalog.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Repository for Product aggregate.
 * Part of the Catalog bounded context — must not be accessed
 * directly from other modules (enforced by Spring Modulith).
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /** Find all active products with pagination */
    Page<Product> findByActiveTrue(Pageable pageable);

    /** Find active products by category */
    Page<Product> findByCategoryIdAndActiveTrue(UUID categoryId, Pageable pageable);

    /** Search products by name or description (case-insensitive) */
    @Query("""
        SELECT p FROM Product p
        WHERE p.active = true
        AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
          OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))
        """)
    Page<Product> searchByQuery(@Param("query") String query, Pageable pageable);
}