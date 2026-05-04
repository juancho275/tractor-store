import { Route } from '@angular/router';

export const appRoutes: Route[] = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/home/home').then((m) => m.HomeComponent),
  },
  {
    path: 'explore',
    loadChildren: () =>
      import('mfeExplore/Routes').then((m) => m.remoteRoutes),
  },
  {
    path: 'decide/:id',
    loadChildren: () =>
      import('mfeDecide/Routes').then((m) => m.remoteRoutes),
  },
  {
    path: 'checkout',
    loadChildren: () =>
      import('mfeCheckout/Routes').then((m) => m.remoteRoutes),
  },
  {
    path: '**',
    redirectTo: '',
  },
];