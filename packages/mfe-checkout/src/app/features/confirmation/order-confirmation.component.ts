import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CheckoutService } from '../../services/checkout.service';
import { PriceComponent } from '../../../../../ts-design-system/src/lib/price/price';

@Component({
  selector: 'app-order-confirmation',
  standalone: true,
  imports: [CommonModule, RouterModule, PriceComponent],
  templateUrl: './order-confirmation.component.html',
  styleUrl: './order-confirmation.component.scss',
})
export class OrderConfirmationComponent implements OnInit {
  protected readonly order = this.checkout.order;

  constructor(
    private readonly checkout: CheckoutService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    if (!this.order()) {
      this.router.navigate(['/checkout']);
    }
  }
}
