import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';
import { OrderSummary } from './orders.models';
import { environment } from '../../environments/environment';

interface OrdersState {
  orders: OrderSummary[];
  loading: boolean;
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class OrdersService {

  private readonly api = `${environment.apiUrl}/api/orders`;

  private readonly _state = signal<OrdersState>({
    orders: [], loading: false, error: null,
  });

  readonly orders  = computed(() => this._state().orders);
  readonly loading = computed(() => this._state().loading);
  readonly error   = computed(() => this._state().error);

  constructor(private http: HttpClient, private auth: AuthService) {}

  loadMyOrders(): void {
    const email = this.auth.currentUser()?.email;
    if (!email) return;

    this._state.update(s => ({ ...s, loading: true, error: null }));

    this.http.get<OrderSummary[]>(`${this.api}/customer/${encodeURIComponent(email)}`).subscribe({
      next: (orders) => {
        const sorted = [...orders].sort((a, b) =>
          (b.createdAt ?? '').localeCompare(a.createdAt ?? '')
        );
        this._state.update(s => ({ ...s, orders: sorted, loading: false }));
      },
      error: () => this._state.update(s => ({
        ...s, loading: false, error: 'No se pudieron cargar tus pedidos.',
      })),
    });
  }
}
