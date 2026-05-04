package com.tractorstore.catalog.domain.repository;

import com.tractorstore.catalog.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Category aggregate.
 * Part of the Catalog bounded context — must not be accessed
 * directly from other modules (enforced by Spring Modulith).
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /** Find a category by its URL-friendly slug */
    Optional<Category> findBySlug(String slug);

    /** Find all top-level categories (no parent) */
    List<Category> findByParentIdIsNull();

    /** Find subcategories of a given parent */
    List<Category> findByParentId(UUID parentId);
}