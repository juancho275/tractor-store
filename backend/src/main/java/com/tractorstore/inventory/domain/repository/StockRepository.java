package com.tractorstore.inventory.domain.repository;

import com.tractorstore.inventory.domain.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Stock aggregate.
 * Part of the Inventory bounded context.
 * Must not be accessed directly from other modules.
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {

    Optional<Stock> findByProductVariantId(UUID productVariantId);

    List<Stock> findByProductVariantIdIn(List<UUID> variantIds);

    @Query("SELECT s FROM Stock s WHERE s.quantity - s.reserved <= :threshold")
    List<Stock> findLowStock(@Param("threshold") int threshold);
}