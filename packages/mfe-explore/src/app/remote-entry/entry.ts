import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

/**
 * Remote entry component for MFE Explore.
 * Acts as the shell container within this micro-frontend.
 * Loaded by the host Shell App via Module Federation at runtime.
 */
@Component({
  selector: 'app-mfe-explore-entry',
  standalone: true,
  imports: [RouterModule],
  template: `<router-outlet />`,
})
export class RemoteEntryComponent {}