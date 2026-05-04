import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

interface QuickCategory {
  name: string;
  slug: string;
  icon: string;
}

/**
 * Home page component — landing page of The Tractor Store.
 * Rendered by the Shell App at the root route '/'.
 * Provides navigation CTAs to the MFE Explore (catalog).
 */
@Component({
  selector: 'ts-home',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './home.html',
  styleUrls: ['./home.scss'],
})
export class HomeComponent {
  protected readonly categories: QuickCategory[] = [
    { name: 'Tractores clásicos', slug: 'clasicos', icon: '🚜' },
    { name: 'Autónomos', slug: 'autonomos', icon: '🤖' },
    { name: 'Cosechadoras', slug: 'cosechadoras', icon: '🌾' },
    { name: 'Accesorios', slug: 'accesorios', icon: '🔧' },
  ];
}