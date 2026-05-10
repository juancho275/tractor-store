import { Route } from '@angular/router';

export const remoteRoutes: Route[] = [
  {
    path: '',
    loadComponent: () =>
      import('../features/cart/cart.component')
        .then(m => m.CartComponent),
  },
];