import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { ToastService } from '../../core/services/toast.service';
import { Cart, CreateOrderRequest } from '../../core/models/models';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  template: `
    <div class="container py-4">
      <h2 class="fw-bold font-serif text-dark mb-4">Checkout & Delivery</h2>

      <div *ngIf="cart && cart.items && cart.items.length > 0" class="row g-4">
        <!-- Shipping Address Form -->
        <div class="col-lg-7">
          <div class="glass-card p-4">
            <h5 class="fw-bold text-dark mb-4 pb-2 border-bottom">
              <i class="fa fa-map-marker-alt text-gold me-2"></i> Shipping Address
            </h5>

            <form [formGroup]="checkoutForm" (ngSubmit)="onPlaceOrder()">
              <div class="row g-3" formGroupName="shippingAddress">
                <div class="col-md-6">
                  <label class="form-label fw-semibold text-dark fs-7">Full Name *</label>
                  <input type="text" class="glass-input" formControlName="fullName" placeholder="e.g. Eleanor Vance">
                  <div *ngIf="isFieldInvalid('shippingAddress.fullName')" class="text-danger fs-7 mt-1">Full name is required.</div>
                </div>

                <div class="col-md-6">
                  <label class="form-label fw-semibold text-dark fs-7">Phone Number *</label>
                  <input type="text" class="glass-input" formControlName="phoneNumber" placeholder="+91 9876543210">
                  <div *ngIf="isFieldInvalid('shippingAddress.phoneNumber')" class="text-danger fs-7 mt-1">Phone number is required.</div>
                </div>

                <div class="col-12">
                  <label class="form-label fw-semibold text-dark fs-7">Address Line 1 *</label>
                  <input type="text" class="glass-input" formControlName="addressLine1" placeholder="House/Flat No, Building, Street">
                  <div *ngIf="isFieldInvalid('shippingAddress.addressLine1')" class="text-danger fs-7 mt-1">Address is required.</div>
                </div>

                <div class="col-12">
                  <label class="form-label fw-semibold text-dark fs-7">Address Line 2 (Optional)</label>
                  <input type="text" class="glass-input" formControlName="addressLine2" placeholder="Landmark, Suite, Apartment">
                </div>

                <div class="col-md-4">
                  <label class="form-label fw-semibold text-dark fs-7">City *</label>
                  <input type="text" class="glass-input" formControlName="city" placeholder="Mumbai">
                  <div *ngIf="isFieldInvalid('shippingAddress.city')" class="text-danger fs-7 mt-1">City is required.</div>
                </div>

                <div class="col-md-4">
                  <label class="form-label fw-semibold text-dark fs-7">State *</label>
                  <input type="text" class="glass-input" formControlName="state" placeholder="Maharashtra">
                  <div *ngIf="isFieldInvalid('shippingAddress.state')" class="text-danger fs-7 mt-1">State is required.</div>
                </div>

                <div class="col-md-4">
                  <label class="form-label fw-semibold text-dark fs-7">Postal Code *</label>
                  <input type="text" class="glass-input" formControlName="postalCode" placeholder="400001">
                  <div *ngIf="isFieldInvalid('shippingAddress.postalCode')" class="text-danger fs-7 mt-1">Postal code is required.</div>
                </div>

                <div class="col-12">
                  <label class="form-label fw-semibold text-dark fs-7">Country *</label>
                  <input type="text" class="glass-input" formControlName="country" readonly>
                </div>
              </div>

              <div class="mt-4 pt-3 border-top">
                <button type="submit" class="glass-button w-100 py-3" [disabled]="isSubmitting">
                  <i *ngIf="isSubmitting" class="fa fa-spinner fa-spin me-2"></i>
                  <i *ngIf="!isSubmitting" class="fa fa-lock me-2"></i>
                  Place Order (Idempotent Protected)
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- Checkout Summary -->
        <div class="col-lg-5">
          <div class="glass-card p-4">
            <h5 class="fw-bold text-dark mb-4 pb-2 border-bottom">Order Details</h5>
            <div class="mb-4" style="max-height: 300px; overflow-y: auto;">
              <div *ngFor="let item of cart.items" class="d-flex justify-content-between align-items-center mb-3">
                <div>
                  <h6 class="fw-semibold text-dark mb-0">{{ item.productName }}</h6>
                  <span class="text-muted fs-7">Qty: {{ item.quantity }} × ₹{{ item.unitPrice | number:'1.0-0' }}</span>
                </div>
                <span class="fw-bold text-gold">₹{{ item.subtotal | number:'1.0-0' }}</span>
              </div>
            </div>

            <hr>

            <div class="d-flex justify-content-between mb-2 text-secondary">
              <span>Subtotal</span>
              <span class="fw-semibold text-dark">₹{{ cart.totalAmount | number:'1.0-0' }}</span>
            </div>
            <div class="d-flex justify-content-between mb-3 text-secondary">
              <span>Insured Express Shipping</span>
              <span class="text-success fw-semibold">FREE</span>
            </div>

            <div class="d-flex justify-content-between pt-3 border-top">
              <span class="fw-bold fs-5 text-dark">Total Amount</span>
              <span class="fw-bold fs-4 text-gold">₹{{ cart.totalAmount | number:'1.0-0' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .text-gold { color: var(--primary-gold); }
    .fs-7 { font-size: 0.85rem; }
  `]
})
export class CheckoutComponent implements OnInit {
  checkoutForm!: FormGroup;
  cart: Cart | null = null;
  isSubmitting = false;
  private idempotencyKey: string = '';

  constructor(
    private fb: FormBuilder,
    private cartService: CartService,
    private orderService: OrderService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.idempotencyKey = this.generateUUID();

    this.checkoutForm = this.fb.group({
      shippingAddress: this.fb.group({
        fullName: ['', Validators.required],
        phoneNumber: ['', Validators.required],
        addressLine1: ['', Validators.required],
        addressLine2: [''],
        city: ['', Validators.required],
        state: ['', Validators.required],
        postalCode: ['', Validators.required],
        country: ['India', Validators.required]
      })
    });

    this.cartService.loadCart().subscribe({
      next: c => {
        this.cart = c;
        if (!c || !c.items || c.items.length === 0) {
          this.router.navigate(['/cart']);
        }
      },
      error: () => this.router.navigate(['/cart'])
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.checkoutForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched || this.isSubmitting));
  }

  onPlaceOrder(): void {
    if (this.checkoutForm.invalid) {
      this.checkoutForm.markAllAsTouched();
      this.toastService.error('Please complete all required shipping fields');
      return;
    }

    this.isSubmitting = true;
    const request: CreateOrderRequest = this.checkoutForm.value;

    this.orderService.createOrder(request, this.idempotencyKey).subscribe({
      next: order => {
        this.isSubmitting = false;
        this.toastService.success('Order placed successfully!');
        this.cartService.cart.set(null); // Clear cart state
        this.router.navigate(['/order-success', order.id], { state: { order } });
      },
      error: err => {
        this.isSubmitting = false;
        this.toastService.error(err.error?.message || 'Order creation failed');
      }
    });
  }

  private generateUUID(): string {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      const r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }
}
