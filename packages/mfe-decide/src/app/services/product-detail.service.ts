import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ProductDetail, ProductVariant, StockInfo } from './product-detail.models';
import { environment } from '../../environments/environment';

interface DecideState {
  product: ProductDetail | null;
  selectedVariant: ProductVariant | null;
  stock: StockInfo | null;
  loading: boolean;
  error: string | null;
  addingToCart: boolean;
}

/**
 * ProductDetailService — manages state for MFE Decide.
 * Fetches product detail and real-time stock from backend APIs.
 * Uses Angular Signals for reactive state management.
 */
@Injectable({ providedIn: 'root' })
export class ProductDetailService {

  private readonly catalogApi = `${environment.apiUrl}/api/catalog`;
  private readonly inventoryApi = `${environment.apiUrl}/api/inventory`;
  private readonly cartApi = `${environment.apiUrl}/api/cart`;

  private readonly _state = signal<DecideState>({
    product: null,
    selectedVariant: null,
    stock: null,
    loading: false,
    error: null,
    addingToCart: false,
  });

  readonly product         = computed(() => this._state().product);
  readonly selectedVariant = computed(() => this._state().selectedVariant);
  readonly stock           = computed(() => this._state().stock);
  readonly loading         = computed(() => this._state().loading);
  readonly error           = computed(() => this._state().error);
  readonly addingToCart    = computed(() => this._state().addingToCart);

  readonly currentPrice = computed(() =>
    this._state().selectedVariant?.price ?? this._state().product?.basePrice ?? 0
  );

  readonly currentImage = computed(() =>
    this._state().selectedVariant?.imageUrl ?? this._state().product?.imageUrl ?? ''
  );

  readonly isInStock = computed(() => {
    const stock = this._state().stock;
    return stock ? stock.available > 0 : true;
  });

  readonly stockStatus = computed(() => this._state().stock?.status ?? 'IN_STOCK');

  constructor(private http: HttpClient) {}

  loadProduct(productId: string): void {
  this.patch({ loading: true, error: null });

  this.http.get<ProductDetail>(`${this.catalogApi}/products/${productId}`)
    .subscribe({
      next: (product) => {
        let firstVariant = product.variants?.[0] ?? null;

        // Productos sin variantes: crear una variante sintética
        if (!firstVariant) {
          firstVariant = {
            id: product.id,
            sku: product.name.replace(/\s+/g, '-').toUpperCase().substring(0, 20),
            name: product.name + ' - Estándar',
            price: product.basePrice,
            imageUrl: product.imageUrl,
            attributes: {},
            active: true,
          };
        }

        this.patch({ product, selectedVariant: firstVariant, loading: false });
        // Solo cargar stock si la variante es real (no sintética)
        if (product.variants?.[0]) this.loadStock(firstVariant.id);
      },
      error: () => this.patch({
        error: 'No se pudo cargar el producto.',
        loading: false,
      }),
    });
}

  selectVariant(variant: ProductVariant): void {
    this.patch({ selectedVariant: variant, stock: null });
    this.loadStock(variant.id);

    // Dispatch variant selected event for cross-MFE communication
    document.dispatchEvent(new CustomEvent('decide:variant-selected', {
      detail: {
        productId: this._state().product?.id,
        variantId: variant.id,
        sku: variant.sku,
        price: variant.price,
      },
      bubbles: true,
      composed: true,
    }));
  }

  addToCart(sessionId: string): void {
    const product = this._state().product;
    const variant = this._state().selectedVariant;
    if (!product || !variant) return;

    this.patch({ addingToCart: true });

    const cartItem = {
      productId: product.id,
      productVariantId: variant.id,
      productName: product.name,
      variantName: variant.name,
      unitPrice: variant.price,
      quantity: 1,
      imageUrl: variant.imageUrl ?? product.imageUrl,
    };

    this.http.post<any>(this.cartApi + '/items', cartItem, {
      headers: { 'X-Session-Id': sessionId }
    }).subscribe({
      next: (cart) => {
        this.patch({ addingToCart: false });
        // Notify Shell header badge via CustomEvent
        document.dispatchEvent(new CustomEvent('checkout:cart-updated', {
          detail: { count: cart.itemCount, total: cart.total },
          bubbles: true,
          composed: true,
        }));
      },
      error: () => this.patch({ addingToCart: false }),
    });
  }

  private loadStock(variantId: string): void {
    this.http.get<StockInfo>(`${this.inventoryApi}/${variantId}`)
      .subscribe({
        next: (stock) => this.patch({ stock }),
        error: () => this.patch({ stock: null }),
      });
  }

  private patch(partial: Partial<DecideState>): void {
    this._state.update(s => ({ ...s, ...partial }));
  }
}