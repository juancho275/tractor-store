import { Component, Input, Output, EventEmitter, computed } from "@angular/core";
import { CommonModule } from "@angular/common";

/**
 * PaginationComponent - page navigation for paginated lists.
 * Emits pageChange when user selects a different page.
 *
 * @example
 * <ts-pagination
 *   [currentPage]="0"
 *   [totalPages]="5"
 *   [totalElements]="53"
 *   [pageSize]="12"
 *   (pageChange)="onPageChange($event)" />
 */
@Component({
  selector: "ts-pagination",
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="ts-pag">
      <span class="ts-pag__info">
        Mostrando {{ rangeStart }}-{{ rangeEnd }} de {{ totalElements }} productos
      </span>
      <div class="ts-pag__controls">
        <button class="ts-pag__btn" [disabled]="currentPage === 0" (click)="go(0)" title="Primera">«</button>
        <button class="ts-pag__btn" [disabled]="currentPage === 0" (click)="go(currentPage - 1)" title="Anterior">‹</button>

        @for (page of visiblePages; track page) {
          @if (page === -1) {
            <span class="ts-pag__dots">...</span>
          } @else {
            <button
              class="ts-pag__btn"
              [class.ts-pag__btn--active]="page === currentPage"
              (click)="go(page)">
              {{ page + 1 }}
            </button>
          }
        }

        <button class="ts-pag__btn" [disabled]="currentPage === totalPages - 1" (click)="go(currentPage + 1)" title="Siguiente">›</button>
        <button class="ts-pag__btn" [disabled]="currentPage === totalPages - 1" (click)="go(totalPages - 1)" title="Última">»</button>
      </div>
    </div>
  `,
  styles: [`
    .ts-pag { display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:12px;padding:16px 0; }
    .ts-pag__info { font-size:13px;color:var(--color-text-muted,#6b7280); }
    .ts-pag__controls { display:flex;align-items:center;gap:4px; }
    .ts-pag__btn { min-width:36px;height:36px;padding:0 8px;border:1.5px solid var(--color-border,#e5e7eb);background:var(--color-bg-primary,#fff);color:var(--color-text-primary,#111827);border-radius:var(--radius-md,8px);font-size:14px;font-weight:500;cursor:pointer;transition:all .15s;font-family:inherit; }
    .ts-pag__btn:hover:not(:disabled) { border-color:var(--color-primary,#2f855a);color:var(--color-primary,#2f855a); }
    .ts-pag__btn--active { background:var(--color-primary,#2f855a);border-color:var(--color-primary,#2f855a);color:#fff; }
    .ts-pag__btn:disabled { opacity:.4;cursor:not-allowed; }
    .ts-pag__dots { padding:0 4px;color:var(--color-text-muted,#6b7280); }
  `]
})
export class PaginationComponent {
  @Input() currentPage = 0;
  @Input() totalPages = 1;
  @Input() totalElements = 0;
  @Input() pageSize = 12;
  @Output() pageChange = new EventEmitter<number>();

  get rangeStart(): number {
    return this.totalElements === 0 ? 0 : this.currentPage * this.pageSize + 1;
  }

  get rangeEnd(): number {
    return Math.min((this.currentPage + 1) * this.pageSize, this.totalElements);
  }

  get visiblePages(): number[] {
    const total = this.totalPages;
    const current = this.currentPage;
    if (total <= 7) return Array.from({ length: total }, (_, i) => i);

    const pages: number[] = [0];
    if (current > 2) pages.push(-1);
    for (let i = Math.max(1, current - 1); i <= Math.min(total - 2, current + 1); i++) {
      pages.push(i);
    }
    if (current < total - 3) pages.push(-1);
    pages.push(total - 1);
    return pages;
  }

  go(page: number): void {
    if (page >= 0 && page < this.totalPages && page !== this.currentPage) {
      this.pageChange.emit(page);
    }
  }
}