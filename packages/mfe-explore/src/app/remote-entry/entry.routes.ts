import { Route } from '@angular/router';

/**
 * Routes exposed by MFE Explore to the Shell App via Module Federation.
 * The Shell loads these routes lazily at the /explore path.
 */
export const remoteRoutes: Route[] = [
  {
    path: '',
    loadComponent: () =>
      import('../features/catalog/catalog.component')
        .then(m => m.CatalogComponent),
  },
];