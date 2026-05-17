import { Route } from '@angular/router';

export const remoteRoutes: Route[] = [
  {
    path: '',
    loadComponent: () =>
      import('../features/cart/cart.component')
        .then(m => m.CartComponent),
  },
  {
    path: 'confirmation',
    loadComponent: () =>
      import('../features/confirmation/order-confirmation.component')
        .then(m => m.OrderConfirmationComponent),
  },
];