import { NxWelcome } from './nx-welcome';
import { Route } from '@angular/router';

export const appRoutes: Route[] = [
  {
    path: 'mfeCheckout',
    loadChildren: () =>
      import('mfeCheckout/Routes').then((m) => m!.remoteRoutes),
  },
  {
    path: 'mfeDecide',
    loadChildren: () => import('mfeDecide/Routes').then((m) => m!.remoteRoutes),
  },
  {
    path: 'mfeExplore',
    loadChildren: () =>
      import('mfeExplore/Routes').then((m) => m!.remoteRoutes),
  },
  {
    path: '',
    component: NxWelcome,
  },
];
