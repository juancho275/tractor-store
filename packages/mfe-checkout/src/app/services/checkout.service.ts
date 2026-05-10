import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Cart, CartItem, OrderRequest, OrderResponse } from './checkout.models';

interface CheckoutState {
  cart: Cart | null;
  loading: boolean;
  placing: boolean;
  error: string | null;
  order: OrderResponse | null;
}

/**
 * CheckoutService — manages cart and order state for MFE Checkout.
 *
 * Fetches cart from backend using session ID from localStorage.
 * Dispatches checkout:cart-updated CustomEvent after any cart change
 * so the Shell header badge stays in sync.
 */
@Injectable({ providedIn: 'root' })
export class CheckoutService {

  private readonly cartApi  = 'http://localhost:8080/api/cart';
  private readonly orderApi = 'http://localhost:8080/api/orders';

  private readonly _state = signal<CheckoutState>({
    cart: null, loading: false,
    placing: false, error: null, order: null,
  });

  readonly cart     = computed(() => this._state().cart);
  readonly items    = computed(() => this._state().cart?.items ?? []);
  readonly total    = computed(() => this._state().cart?.total ?? 0);
  readonly count    = computed(() => this._state().cart?.itemCount ?? 0);
  readonly loading  = computed(() => this._state().loading);
  readonly placing  = computed(() => this._state().placing);
  readonly error    = computed(() => this._state().error);
  readonly order    = computed(() => this._state().order);
  readonly hasItems = computed(() => (this._state().cart?.items?.length ?? 0) > 0);

  constructor(private http: HttpClient) {}

  loadCart(): void {
    // Resetear order al cargar — permite nuevo ciclo de compra
    this.patch({ loading: true, error: null, order: null });
    this.http.get<Cart>(this.cartApi, {
        headers: { 'X-Session-Id': this.sessionId }
    }).subscribe({
        next: (cart) => {
        this.patch({ cart, loading: false });
        this.notifyShell(cart.itemCount, cart.total);
        },
        error: () => this.patch({ error: 'Error al cargar el carrito.', loading: false }),
    });
    }

  updateQuantity(itemId: string, quantity: number): void {
    this.http.put<Cart>(`${this.cartApi}/items/${itemId}`, null, {
      params: { quantity: quantity.toString() },
      headers: { 'X-Session-Id': this.sessionId }
    }).subscribe({
      next: (cart) => {
        this.patch({ cart });
        this.notifyShell(cart.itemCount, cart.total);
      }
    });
  }

  removeItem(itemId: string): void {
    this.http.delete<Cart>(`${this.cartApi}/items/${itemId}`, {
      headers: { 'X-Session-Id': this.sessionId }
    }).subscribe({
      next: (cart) => {
        this.patch({ cart });
        this.notifyShell(cart.itemCount, cart.total);
      }
    });
  }

  placeOrder(customerEmail: string, customerName: string, shippingAddress: string): void {
    const items = this.items();
    if (!items.length) return;

    this.patch({ placing: true, error: null });

    const request: OrderRequest = {
      customerEmail, customerName, shippingAddress,
      items: items.map(i => ({
        productVariantId: i.productVariantId,
        productName: i.productName,
        variantName: i.variantName,
        unitPrice: i.unitPrice,
        quantity: i.quantity,
      }))
    };

    

    this.http.post<OrderResponse>(this.orderApi, request).subscribe({
      next: (order) => {
        // Limpiar carrito en backend tras orden exitosa
        this.http.delete<Cart>(this.cartApi, {
            headers: { 'X-Session-Id': this.sessionId }
        }).subscribe();

        this.patch({ order, placing: false, cart: null });
        this.notifyShell(0, 0);
        },
      error: () => this.patch({
        error: 'Error al procesar el pedido. Intenta de nuevo.',
        placing: false,
      }),
    });
  }

  get sessionId(): string {
    let s = localStorage.getItem('ts-session-id');
    if (!s) {
      s = 'sess-' + Math.random().toString(36).substring(2);
      localStorage.setItem('ts-session-id', s);
    }
    return s;
  }

  private notifyShell(count: number, total: number): void {
    document.dispatchEvent(new CustomEvent('checkout:cart-updated', {
      detail: { count, total },
      bubbles: true, composed: true,
    }));
  }

  private patch(p: Partial<CheckoutState>): void {
    this._state.update(s => ({ ...s, ...p }));
  }
}