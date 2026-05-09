import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ProductDetailService } from '../../services/product-detail.service';
import { ProductVariant } from '../../services/product-detail.models';
import { BadgeComponent } from '../../../../../ts-design-system/src/lib/badge/badge';
import { ButtonComponent } from '../../../../../ts-design-system/src/lib/button/button';
import { PriceComponent } from '../../../../../ts-design-system/src/lib/price/price';
import { SpinnerComponent } from '../../../../../ts-design-system/src/lib/spinner/spinner';

/**
 * ProductDetailComponent — main view of MFE Decide.
 *
 * Displays full product detail with variant selector and stock indicator.
 * Adds items to cart via CartService and notifies Shell via CustomEvent.
 */
@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, BadgeComponent, ButtonComponent, PriceComponent, SpinnerComponent],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss',
})
export class ProductDetailComponent implements OnInit {

  protected readonly product        = this.service.product;
  protected readonly selectedVariant = this.service.selectedVariant;
  protected readonly stock          = this.service.stock;
  protected readonly loading        = this.service.loading;
  protected readonly error          = this.service.error;
  protected readonly currentPrice   = this.service.currentPrice;
  protected readonly currentImage   = this.service.currentImage;
  protected readonly isInStock      = this.service.isInStock;
  protected readonly stockStatus    = this.service.stockStatus;
  protected readonly addingToCart   = this.service.addingToCart;

  protected readonly sessionId = signal(this.getOrCreateSession());
  protected addedFeedback = false;

  constructor(
    private route: ActivatedRoute,
    protected service: ProductDetailService
  ) {}

  ngOnInit(): void {
    const productId = this.route.snapshot.paramMap.get('id');
    if (productId) this.service.loadProduct(productId);
  }

  onVariantSelect(variant: ProductVariant): void {
    this.service.selectVariant(variant);
  }

  onAddToCart(): void {
    this.service.addToCart(this.sessionId());
    this.addedFeedback = true;
    setTimeout(() => this.addedFeedback = false, 2000);
  }

  isSelectedVariant(variant: ProductVariant): boolean {
    return this.selectedVariant()?.id === variant.id;
  }

  getStockBadgeVariant(): 'success' | 'warning' | 'danger' {
    const status = this.stockStatus();
    if (status === 'IN_STOCK') return 'success';
    if (status === 'LOW_STOCK') return 'warning';
    return 'danger';
  }

  getStockLabel(): string {
    const status = this.stockStatus();
    const available = this.stock()?.available ?? 0;
    if (status === 'OUT_OF_STOCK') return 'Agotado';
    if (status === 'LOW_STOCK') return `Pocas unidades (${available})`;
    return 'En stock';
  }

  protected objectEntries(obj: Record<string, string>): [string, string][] {
    return Object.entries(obj);
  }

  private getOrCreateSession(): string {
    let session = localStorage.getItem('ts-session-id');
    if (!session) {
      session = 'sess-' + Math.random().toString(36).substring(2);
      localStorage.setItem('ts-session-id', session);
    }
    return session;
  }
}