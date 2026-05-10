import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CheckoutService } from '../../services/checkout.service';
import { ButtonComponent } from '../../../../../ts-design-system/src/lib/button/button';
import { PriceComponent } from '../../../../../ts-design-system/src/lib/price/price';
import { SpinnerComponent } from '../../../../../ts-design-system/src/lib/spinner/spinner';

/**
 * CartComponent — main view of MFE Checkout.
 *
 * Shows cart items, allows quantity changes and removal.
 * Contains the checkout form for customer data and order placement.
 * Dispatches checkout:cart-updated CustomEvent after every cart mutation.
 */
@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule,
            ButtonComponent, PriceComponent, SpinnerComponent],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss',
})
export class CartComponent implements OnInit {

  protected readonly items    = this.checkout.items;
  protected readonly total    = this.checkout.total;
  protected readonly count    = this.checkout.count;
  protected readonly loading  = this.checkout.loading;
  protected readonly placing  = this.checkout.placing;
  protected readonly error    = this.checkout.error;
  protected readonly order    = this.checkout.order;
  protected readonly hasItems = this.checkout.hasItems;

  protected readonly form = this.fb.nonNullable.group({
    customerName:    ['', [Validators.required, Validators.minLength(3)]],
    customerEmail:   ['', [Validators.required, Validators.email]],
    shippingAddress: ['', [Validators.required, Validators.minLength(10)]],
  });

  constructor(
    private checkout: CheckoutService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.checkout.loadCart();
  }

  onIncrease(itemId: string, current: number): void {
    this.checkout.updateQuantity(itemId, current + 1);
  }

  onDecrease(itemId: string, current: number): void {
    if (current > 1) this.checkout.updateQuantity(itemId, current - 1);
    else this.checkout.removeItem(itemId);
  }

  onRemove(itemId: string): void {
    this.checkout.removeItem(itemId);
  }

  onPlaceOrder(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { customerName, customerEmail, shippingAddress } = this.form.getRawValue();
    this.checkout.placeOrder(customerEmail, customerName, shippingAddress);
  }

  hasError(field: string): boolean {
    const control = this.form.get(field);
    return !!(control?.invalid && control?.touched);
  }
}