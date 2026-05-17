import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { OrdersService } from '../../orders/orders.service';
import { PriceComponent } from '@tractor-store-setup/tsDesignSystem';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, RouterModule, PriceComponent],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.scss',
})
export class OrdersComponent implements OnInit {
  protected readonly orders  = this.ordersService.orders;
  protected readonly loading = this.ordersService.loading;
  protected readonly error   = this.ordersService.error;

  protected expanded = new Set<string>();

  constructor(private ordersService: OrdersService) {}

  ngOnInit(): void {
    this.ordersService.loadMyOrders();
  }

  toggle(id: string): void {
    this.expanded.has(id) ? this.expanded.delete(id) : this.expanded.add(id);
  }

  isExpanded(id: string): boolean {
    return this.expanded.has(id);
  }

  statusLabel(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'Pendiente', CONFIRMED: 'Confirmado', CANCELLED: 'Cancelado',
    };
    return map[status] ?? status;
  }
}
