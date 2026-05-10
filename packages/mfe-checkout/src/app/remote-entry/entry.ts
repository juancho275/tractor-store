import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-mfe-checkout-entry',
  standalone: true,
  imports: [RouterModule],
  template: `<router-outlet />`,
})
export class RemoteEntryComponent {}