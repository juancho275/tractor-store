import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger';
export type ButtonSize = 'sm' | 'md' | 'lg';

/**
 * ButtonComponent — base button for The Tractor Store Design System.
 *
 * Supports multiple variants and sizes via CSS Custom Properties.
 * Used across all MFEs (Explore, Decide, Checkout).
 *
 * @example
 * <ts-button variant="primary" size="md" (clicked)="onSubmit()">
 *   Ver catálogo
 * </ts-button>
 */
@Component({
  selector: 'ts-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button
      [class]="buttonClasses"
      [disabled]="disabled || loading"
      [type]="type"
      (click)="onClick()">
      @if (loading) {
        <span class="ts-btn__spinner"></span>
      }
      <ng-content />
    </button>
  `,
  styles: [`
    .ts-btn {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      border: none;
      border-radius: var(--radius-md, 8px);
      font-weight: 600;
      cursor: pointer;
      transition: all 0.15s ease;
      text-decoration: none;
      font-family: inherit;

      &:disabled { opacity: 0.5; cursor: not-allowed; }

      /* Sizes */
      &--sm { padding: 6px 12px; font-size: 13px; }
      &--md { padding: 10px 20px; font-size: 15px; }
      &--lg { padding: 14px 28px; font-size: 16px; }

      /* Variants */
      &--primary {
        background: var(--btn-primary-bg, #2f855a);
        color: var(--btn-primary-color, #fff);
        &:hover:not(:disabled) { background: var(--btn-primary-hover, #276749); }
      }
      &--secondary {
        background: transparent;
        color: var(--color-text-primary, #111827);
        border: 2px solid var(--color-border, #e5e7eb);
        &:hover:not(:disabled) {
          border-color: var(--color-primary, #2f855a);
          color: var(--color-primary, #2f855a);
        }
      }
      &--ghost {
        background: transparent;
        color: var(--color-primary, #2f855a);
        &:hover:not(:disabled) { background: var(--color-primary-light, #c6f6d5); }
      }
      &--danger {
        background: #dc2626;
        color: #fff;
        &:hover:not(:disabled) { background: #b91c1c; }
      }
    }

    .ts-btn__spinner {
      width: 14px; height: 14px;
      border: 2px solid currentColor;
      border-top-color: transparent;
      border-radius: 50%;
      animation: spin 0.7s linear infinite;
    }

    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class ButtonComponent {
  @Input() variant: ButtonVariant = 'primary';
  @Input() size: ButtonSize = 'md';
  @Input() disabled = false;
  @Input() loading = false;
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Output() clicked = new EventEmitter<void>();

  get buttonClasses(): string {
    return `ts-btn ts-btn--${this.variant} ts-btn--${this.size}`;
  }

  onClick(): void {
    if (!this.disabled && !this.loading) {
      this.clicked.emit();
    }
  }
}