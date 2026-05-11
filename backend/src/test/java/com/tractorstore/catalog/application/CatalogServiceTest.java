package com.tractorstore.catalog.application;

import com.tractorstore.catalog.application.dto.CategoryResponse;
import com.tractorstore.catalog.application.dto.ProductResponse;
import com.tractorstore.catalog.application.dto.ProductSummaryResponse;
import com.tractorstore.catalog.domain.model.Category;
import com.tractorstore.catalog.domain.model.Product;
import com.tractorstore.catalog.domain.repository.CategoryRepository;
import com.tractorstore.catalog.domain.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CatalogService")
class CatalogServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @InjectMocks private CatalogService catalogService;

    private Product product;
    private Category category;
    private UUID productId;
    private UUID categoryId;
    private PageRequest pageable;

    @BeforeEach
void setUp() {
    productId  = UUID.randomUUID();
    categoryId = UUID.randomUUID();
    pageable   = PageRequest.of(0, 12);

    // Mockear entidades JPA (solo tienen getters)
    category = mock(Category.class);
    when(category.getId()).thenReturn(categoryId);
    when(category.getName()).thenReturn("Tractores");
    when(category.getSlug()).thenReturn("tractores");
    when(category.getImageUrl()).thenReturn(null);
    when(category.getParentId()).thenReturn(null);

    product = mock(Product.class);
    when(product.getId()).thenReturn(productId);
    when(product.getName()).thenReturn("TractorPro X200");
    when(product.getDescription()).thenReturn("Tractor de alta potencia");
    when(product.getBasePrice()).thenReturn(new BigDecimal("45000000"));
    when(product.getCategoryId()).thenReturn(categoryId);
    when(product.getImageUrl()).thenReturn(null);
    when(product.getTags()).thenReturn("tractor");
    when(product.isActive()).thenReturn(true);
    when(product.getVariants()).thenReturn(new ArrayList<>());
    when(product.getCreatedAt()).thenReturn(null);
    when(product.getUpdatedAt()).thenReturn(null);
}

    @Nested
    @DisplayName("getProducts()")
    class GetProducts {

        @Test
        @DisplayName("returns paginated product summaries")
        void returnsPaginatedProducts() {
            Page<Product> page = new PageImpl<>(List.of(product));
            when(productRepository.findByActiveTrue(pageable)).thenReturn(page);

            Page<ProductSummaryResponse> result = catalogService.getProducts(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).name()).isEqualTo("TractorPro X200");
        }

        @Test
        @DisplayName("returns empty page when no products exist")
        void returnsEmptyPageWhenNoProducts() {
            when(productRepository.findByActiveTrue(pageable))
                .thenReturn(new PageImpl<>(List.of()));

            Page<ProductSummaryResponse> result = catalogService.getProducts(pageable);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("maps product fields correctly to summary response")
        void mapsProductFieldsCorrectly() {
            Page<Product> page = new PageImpl<>(List.of(product));
            when(productRepository.findByActiveTrue(pageable)).thenReturn(page);

            ProductSummaryResponse summary = catalogService.getProducts(pageable)
                .getContent().get(0);

            assertThat(summary.id()).isEqualTo(productId);
            assertThat(summary.basePrice()).isEqualByComparingTo("45000000");
        }
    }

    @Nested
    @DisplayName("getProduct()")
    class GetProduct {

        @Test
        @DisplayName("returns product detail when found")
        void returnsProductWhenFound() {
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            ProductResponse response = catalogService.getProduct(productId);

            assertThat(response.name()).isEqualTo("TractorPro X200");
            assertThat(response.id()).isEqualTo(productId);
        }

        @Test
        @DisplayName("returns empty variants list when product has no variants")
        void returnsEmptyVariantsWhenNone() {
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            ProductResponse response = catalogService.getProduct(productId);

            assertThat(response.variants()).isEmpty();
        }

        @Test
        @DisplayName("throws EntityNotFoundException when product not found")
        void throwsWhenProductNotFound() {
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> catalogService.getProduct(productId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(productId.toString());
        }
    }

    @Nested
    @DisplayName("getProductsByCategory()")
    class GetProductsByCategory {

        @Test
        @DisplayName("returns products filtered by category")
        void returnsProductsFilteredByCategory() {
            Page<Product> page = new PageImpl<>(List.of(product));
            when(productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable))
                .thenReturn(page);

            Page<ProductSummaryResponse> result =
                catalogService.getProductsByCategory(categoryId, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(productRepository).findByCategoryIdAndActiveTrue(categoryId, pageable);
        }

        @Test
        @DisplayName("returns empty page for category with no products")
        void returnsEmptyForEmptyCategory() {
            when(productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable))
                .thenReturn(new PageImpl<>(List.of()));

            var result = catalogService.getProductsByCategory(categoryId, pageable);

            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("searchProducts()")
    class SearchProducts {

        @Test
        @DisplayName("returns matching products for search term")
        void returnsMatchingProducts() {
            Page<Product> page = new PageImpl<>(List.of(product));
            when(productRepository.searchByQuery(eq("TractorPro"), any()))
                .thenReturn(page);

            var result = catalogService.searchProducts("TractorPro", pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).name()).contains("TractorPro");
        }

        @Test
        @DisplayName("returns empty page when no results match")
        void returnsEmptyWhenNoMatch() {
            when(productRepository.searchByQuery(eq("xyz123"), any()))
                .thenReturn(new PageImpl<>(List.of()));

            var result = catalogService.searchProducts("xyz123", pageable);

            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getCategories()")
    class GetCategories {

        @Test
        @DisplayName("returns all categories")
        void returnsAllCategories() {
            when(categoryRepository.findAll()).thenReturn(List.of(category));

            List<CategoryResponse> result = catalogService.getCategories();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Tractores");
            assertThat(result.get(0).slug()).isEqualTo("tractores");
        }

        @Test
        @DisplayName("returns empty list when no categories exist")
        void returnsEmptyListWhenNoCategories() {
            when(categoryRepository.findAll()).thenReturn(List.of());

            assertThat(catalogService.getCategories()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getCategoryBySlug()")
    class GetCategoryBySlug {

        @Test
        @DisplayName("returns category when slug found")
        void returnsCategoryWhenFound() {
            when(categoryRepository.findBySlug("tractores"))
                .thenReturn(Optional.of(category));

            CategoryResponse response = catalogService.getCategoryBySlug("tractores");

            assertThat(response.name()).isEqualTo("Tractores");
            assertThat(response.slug()).isEqualTo("tractores");
        }

        @Test
        @DisplayName("throws EntityNotFoundException when slug not found")
        void throwsWhenSlugNotFound() {
            when(categoryRepository.findBySlug("unknown"))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() -> catalogService.getCategoryBySlug("unknown"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("unknown");
        }
    }
}