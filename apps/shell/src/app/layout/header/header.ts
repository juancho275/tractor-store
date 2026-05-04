import { Component, signal, computed } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MFEEvents } from '@tractor-store-setup/sharedCatalog';
import { CartUpdatedPayload } from '@tractor-store-setup/sharedCatalog';

/**
 * Global Header component for The Tractor Store Shell.
 * Listens to checkout:cart-updated CustomEvent from MFE Checkout
 * and updates the cart badge reactively using Angular Signals.
 *
 * Communication pattern: CustomEvent (composed: true) dispatched
 * by MFE Checkout, received here via document event listener.
 */
@Component({
  selector: 'ts-header',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class HeaderComponent {
  /** Reactive cart item count — updated via CustomEvent from MFE Checkout */
  protected readonly cartCount = signal(0);

  /** Show badge only when cart has items */
  protected readonly showBadge = computed(() => this.cartCount() > 0);

  constructor() {
    // Listen for cart updates from MFE Checkout via CustomEvent
    // composed: true allows the event to cross Shadow DOM boundaries
    document.addEventListener(
      MFEEvents.CART_UPDATED,
      this.onCartUpdated.bind(this)
    );
  }

  /**
   * Handles the checkout:cart-updated CustomEvent dispatched by MFE Checkout.
   * Updates the cart badge count reactively.
   *
   * @param event - CustomEvent containing CartUpdatedPayload
   */
  private onCartUpdated(event: Event): void {
    const payload = (event as CustomEvent<CartUpdatedPayload>).detail;
    this.cartCount.set(payload.count);
  }

  ngOnDestroy(): void {
    document.removeEventListener(
      MFEEvents.CART_UPDATED,
      this.onCartUpdated.bind(this)
    );
  }
}