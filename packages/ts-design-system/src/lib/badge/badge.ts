import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export type BadgeVariant = 'success' | 'warning' | 'danger' | 'info' | 'neutral';

/**
 * BadgeComponent — small label for status indicators.
 *
 * Used for stock status, order states, and category tags.
 *
 * @example
 * <ts-badge variant="success">En stock</ts-badge>
 * <ts-badge variant="danger">Agotado</ts-badge>
 */
@Component({
  selector: 'ts-badge',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span [class]="badgeClasses">
      <ng-content />
    </span>
  `,
  styles: [`
    .ts-badge {
      display: inline-flex;
      align-items: center;
      padding: 2px 10px;
      border-radius: var(--radius-full, 9999px);
      font-size: 12px;
      font-weight: 600;
      letter-spacing: 0.02em;

      &--success  { background: #d1fae5; color: #065f46; }
      &--warning  { background: #fef3c7; color: #92400e; }
      &--danger   { background: #fee2e2; color: #991b1b; }
      &--info     { background: #dbeafe; color: #1e40af; }
      &--neutral  { background: var(--color-gray-100, #f3f4f6); color: var(--color-gray-700, #374151); }
    }
  `]
})
export class BadgeComponent {
  @Input() variant: BadgeVariant = 'neutral';

  get badgeClasses(): string {
    return `ts-badge ts-badge--${this.variant}`;
  }
}