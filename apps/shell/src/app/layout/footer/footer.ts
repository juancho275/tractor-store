import { Component } from '@angular/core';

/**
 * Global Footer component for The Tractor Store Shell.
 * Shared across all micro-frontend routes.
 */
@Component({
  selector: 'ts-footer',
  standalone: true,
  templateUrl: './footer.html',
  styleUrl: './footer.scss',
})
export class FooterComponent {
  protected readonly currentYear = new Date().getFullYear();
}