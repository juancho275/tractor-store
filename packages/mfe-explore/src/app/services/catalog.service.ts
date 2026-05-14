import { Injectable, signal, computed } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ProductSummary, Category } from './catalog.models';

export interface CatalogState {
  totalPages: number;
  products: ProductSummary[];
  categories: Category[];
  loading: boolean;
  error: string | null;
  totalElements: number;
  currentPage: number;
  selectedCategory: string | null;
  searchQuery: string;
}

/**
 * CatalogService — manages product catalog state using Angular Signals.
 *
 * Fetches data from the Spring Boot Catalog REST API.
 * Each MFE manages its own state locally — no shared global store.
 *
 * @see http://localhost:8080/api/catalog/products
 */
@Injectable({ providedIn: 'root' })
export class CatalogService {

  private readonly apiUrl = 'https://tractor-store-production.up.railway.app/api/catalog';

  // ── State as Signals ──────────────────────────────────────────────────────
  private readonly _state = signal<CatalogState>({
    products: [],
    categories: [],
    loading: false,
    error: null,
    totalElements: 0,
    currentPage: 0,
    selectedCategory: null,
    searchQuery: '',
    totalPages: 1,
  });

  // ── Public readonly Signals ───────────────────────────────────────────────
  readonly products    = computed(() => this._state().products);
  readonly categories  = computed(() => this._state().categories);
  readonly loading     = computed(() => this._state().loading);
  readonly error       = computed(() => this._state().error);
  readonly totalElements = computed(() => this._state().totalElements);
  readonly hasProducts = computed(() => this._state().products.length > 0);
  readonly selectedCategory = computed(() => this._state().selectedCategory);
  readonly searchQuery = computed(() => this._state().searchQuery);
  readonly totalPages = computed(() => this._state().totalPages);
  readonly currentPage = computed(() => this._state().currentPage);

  constructor(private http: HttpClient) {}

  /**
   * Loads products from the Catalog API.
   * Applies category filter and search query if set.
   *
   * @param page - page number (0-based)
   * @param size - page size
   */
  loadProducts(page = 0, size = 12): void {
    this.patch({ loading: true, error: null });

    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', 'name');

    const state = this._state();
    if (state.selectedCategory) {
      params = params.set('category', state.selectedCategory);
    }
    if (state.searchQuery) {
      params = params.set('q', state.searchQuery);
    }

    this.http.get<any>(`${this.apiUrl}/products`, { params })
      .subscribe({
        next: (res) => this.patch({
          products: res.content ?? [],
          totalElements: res.totalElements ?? 0,
          totalPages: res.totalPages ?? 1,
          currentPage: page,
          loading: false,
        }),
        error: (err) => this.patch({
          error: 'Error al cargar productos. Intenta de nuevo.',
          loading: false,
        }),
      });
  }

  /**
   * Loads all categories from the Catalog API.
   */
  loadCategories(): void {
    this.http.get<Category[]>(`${this.apiUrl}/categories`)
      .subscribe({
        next: (cats) => this.patch({ categories: cats }),
        error: () => this.patch({ categories: [] }),
      });
  }

  /**
   * Filters products by category.
   * Resets to page 0 when category changes.
   *
   * @param categoryId - UUID of the category, or null to show all
   */
  filterByCategory(categoryId: string | null): void {
    this.patch({ selectedCategory: categoryId, currentPage: 0 });
    this.loadProducts(0);
  }

  /**
   * Searches products by query string.
   * Resets to page 0 on new search.
   *
   * @param query - search term
   */
  search(query: string): void {
    this.patch({ searchQuery: query, currentPage: 0 });
    this.loadProducts(0);
  }

  /** Clears all filters and reloads */
  clearFilters(): void {
    this.patch({ selectedCategory: null, searchQuery: '' });
    this.loadProducts(0);
  }

  private patch(partial: Partial<CatalogState>): void {
    this._state.update(s => ({ ...s, ...partial }));
  }
}