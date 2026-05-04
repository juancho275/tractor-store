import { Component, Input } from '@angular/core';

export type SpinnerSize = 'sm' | 'md' | 'lg';

/**
 * SpinnerComponent — loading indicator.
 *
 * Used during API calls and lazy-loaded MFE loading states.
 *
 * @example
 * <ts-spinner size="md" />
 */
@Component({
  selector: 'ts-spinner',
  standalone: true,
  template: `<div [class]="spinnerClasses" role="status" aria-label="Cargando..."></div>`,
  styles: [`
    .ts-spinner {
      border-radius: 50%;
      border: 3px solid var(--color-border, #e5e7eb);
      border-top-color: var(--color-primary, #2f855a);
      animation: ts-spin 0.7s linear infinite;

      &--sm { width: 16px; height: 16px; }
      &--md { width: 28px; height: 28px; border-width: 3px; }
      &--lg { width: 44px; height: 44px; border-width: 4px; }
    }
    @keyframes ts-spin { to { transform: rotate(360deg); } }
  `]
})
export class SpinnerComponent {
  @Input() size: SpinnerSize = 'md';

  get spinnerClasses(): string {
    return `ts-spinner ts-spinner--${this.size}`;
  }
}