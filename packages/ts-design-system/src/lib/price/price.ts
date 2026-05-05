import { Component, Input, OnChanges } from '@angular/core';

/**
 * PriceComponent — formats monetary values in Colombian Pesos (COP).
 *
 * Handles large tractor prices (millions) with proper formatting.
 * Uses Intl.NumberFormat for locale-aware currency display.
 *
 * @example
 * <ts-price [amount]="45000000" />
 * <!-- Displays: $45.000.000 -->
 */
@Component({
  selector: 'ts-price',
  standalone: true,
  template: `
    <span class="ts-price" [class.ts-price--large]="large">
      {{ formattedPrice }}
    </span>
  `,
  styles: [`
    .ts-price {
      font-weight: 700;
      color: var(--color-text-primary, #111827);
      font-size: 1em;

      &--large { font-size: 1.5em; }
    }
  `]
})
export class PriceComponent implements OnChanges {
  @Input() amount = 0;
  @Input() currency = 'COP';
  @Input() large = false;

  formattedPrice = '';

  ngOnChanges(): void {
    this.formattedPrice = new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: this.currency,
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(this.amount);
  }
}