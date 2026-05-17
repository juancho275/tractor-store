import { Route } from '@angular/router';
import { authGuard } from './auth/auth.guard';

export const appRoutes: Route[] = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/home/home').then((m) => m.HomeComponent),
  },
  {
    path: 'auth/login',
    loadComponent: () =>
      import('./pages/auth/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'auth/register',
    loadComponent: () =>
      import('./pages/auth/register/register.component').then((m) => m.RegisterComponent),
  },
  {
    path: 'orders',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/orders/orders.component').then((m) => m.OrdersComponent),
  },
  {
    path: 'explore',
    loadChildren: () =>
      import('mfeExplore/Routes').then((m) => m.remoteRoutes),
  },
  {
    path: 'decide',
    loadChildren: () =>
      import('mfeDecide/Routes').then((m) => m.remoteRoutes),
  },
  {
    path: 'checkout',
    canActivate: [authGuard],
    loadChildren: () =>
      import('mfeCheckout/Routes').then((m) => m.remoteRoutes),
  },
  {
    path: '**',
    redirectTo: '',
  },
];