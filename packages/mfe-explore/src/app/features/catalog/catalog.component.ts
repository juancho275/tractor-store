import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CatalogService } from '../../services/catalog.service';
import { PaginationComponent } from '../../../../../ts-design-system/src/lib/pagination/pagination';
import {
  CardComponent,
  SpinnerComponent,
  InputComponent,
  BadgeComponent,
} from '@tractor-store-setup/tsDesignSystem';

/**
 * CatalogComponent — main view of MFE Explore.
 *
 * Displays the product grid with category filters and search.
 * All state is managed via Angular Signals through CatalogService.
 * Loaded lazily by the Shell App via Module Federation.
 */
@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardComponent,
    SpinnerComponent,
    InputComponent,
    BadgeComponent,
    PaginationComponent,
  ],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.scss',
})
export class CatalogComponent implements OnInit {
  protected readonly products    = this.catalog.products;
  protected readonly categories  = this.catalog.categories;
  protected readonly loading     = this.catalog.loading;
  protected readonly error       = this.catalog.error;
  protected readonly hasProducts = this.catalog.hasProducts;
  protected readonly selectedCategory = this.catalog.selectedCategory;
  protected readonly searchQuery = this.catalog.searchQuery;
  protected readonly totalPages  = this.catalog.totalPages;
  protected readonly currentPage = this.catalog.currentPage;

  constructor(protected readonly catalog: CatalogService) {}

  ngOnInit(): void {
    this.catalog.loadCategories();
    this.catalog.loadProducts();
  }

  onCategoryClick(categoryId: string): void {
    const current = this.selectedCategory();
    this.catalog.filterByCategory(current === categoryId ? null : categoryId);
  }

  onSearch(query: string): void {
    this.catalog.search(query);
  }

  onClearFilters(): void {
    this.catalog.clearFilters();
  }

  onPageChange(page: number): void {
    this.catalog.loadProducts(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  } 

  trackByProduct(_: number, product: any): string {
    return product.id;
  }

  trackByCategory(_: number, cat: any): string {
    return cat.id;
  }
}