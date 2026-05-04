import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PriceComponent } from '../price/price';
import { BadgeComponent } from '../badge/badge';

/**
 * CardComponent — product card for catalog grids.
 *
 * Displays product image, name, price and stock status.
 * Navigates to product detail on click via routerLink.
 *
 * @example
 * <ts-card
 *   [productId]="product.id"
 *   [name]="product.name"
 *   [price]="product.basePrice"
 *   [imageUrl]="product.imageUrl"
 *   [inStock]="true" />
 */
@Component({
  selector: 'ts-card',
  standalone: true,
  imports: [CommonModule, RouterModule, PriceComponent, BadgeComponent],
  template: `
    <a [routerLink]="['/decide', productId]" class="ts-card">
      <div class="ts-card__image-wrapper">
        <img
          [src]="imageUrl || fallbackImage"
          [alt]="name"
          class="ts-card__image"
          loading="lazy" />
        <div class="ts-card__badge">
          <ts-badge [variant]="inStock ? 'success' : 'danger'">
            {{ inStock ? 'En stock' : 'Agotado' }}
          </ts-badge>
        </div>
      </div>
      <div class="ts-card__body">
        <h3 class="ts-card__name">{{ name }}</h3>
        @if (description) {
          <p class="ts-card__description">{{ description }}</p>
        }
        <div class="ts-card__footer">
          <ts-price [amount]="price" />
        </div>
      </div>
    </a>
  `,
  styles: [`
    .ts-card {
      display: flex;
      flex-direction: column;
      background: var(--card-bg, #fff);
      border: 1px solid var(--card-border, #e5e7eb);
      border-radius: var(--card-radius, 8px);
      box-shadow: var(--card-shadow, 0 4px 6px rgba(0,0,0,0.07));
      text-decoration: none;
      overflow: hidden;
      transition: all 0.2s ease;

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 10px 15px rgba(0,0,0,0.1);
        border-color: var(--color-primary, #2f855a);
      }

      &__image-wrapper {
        position: relative;
        aspect-ratio: 4/3;
        overflow: hidden;
        background: var(--color-bg-secondary, #f9fafb);
      }

      &__image {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s ease;
      }

      &:hover &__image { transform: scale(1.04); }

      &__badge {
        position: absolute;
        top: 10px;
        right: 10px;
      }

      &__body {
        padding: 16px;
        display: flex;
        flex-direction: column;
        gap: 6px;
        flex: 1;
      }

      &__name {
        font-size: 15px;
        font-weight: 600;
        color: var(--color-text-primary, #111827);
        margin: 0;
        line-height: 1.3;
      }

      &__description {
        font-size: 13px;
        color: var(--color-text-muted, #6b7280);
        margin: 0;
        line-height: 1.4;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      &__footer {
        margin-top: auto;
        padding-top: 10px;
      }
    }
  `]
})
export class CardComponent {
  @Input() productId = '';
  @Input() name = '';
  @Input() description = '';
  @Input() price = 0;
  @Input() imageUrl = '';
  @Input() inStock = true;

  readonly fallbackImage = 'https://images.unsplash.com/photo-1592984791259-da6ea0b2f4ca?w=400';
}