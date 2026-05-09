import { Route } from '@angular/router';

export const remoteRoutes: Route[] = [
  {
    path: ':id',
    loadComponent: () =>
      import('../features/product-detail/product-detail.component')
        .then(m => m.ProductDetailComponent),
  },
];