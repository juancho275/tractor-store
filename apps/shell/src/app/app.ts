import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from './layout/header/header';
import { FooterComponent } from './layout/footer/footer';

/**
 * Root component of The Tractor Store Shell App.
 * Acts as the Module Federation host — orchestrates
 * all micro-frontend remotes via lazy-loaded routes.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule, HeaderComponent, FooterComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = 'The Tractor Store';
}