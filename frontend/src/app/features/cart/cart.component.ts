import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../core/services/cart.service';
import { ToastService } from '../../core/services/toast.service';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';
import { Cart, CartItem } from '../../core/models/models';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, EmptyStateComponent, ConfirmDialogComponent],
  template: `
    <div class="container py-4">
      <h2 class="fw-bold font-serif text-dark mb-4">Your Shopping Cart</h2>

      <!-- Loading State -->
      <div *ngIf="isLoading" class="glass-card p-5 text-center">
        <i class="fa fa-spinner fa-spin text-gold fs-1 mb-3"></i>
        <p class="text-muted">Loading your cart items...</p>
      </div>

      <!-- Empty Cart -->
      <app-empty-state 
        *ngIf="!isLoading && (!cart || !cart.items || cart.items.length === 0)"
        icon="fa fa-shopping-bag"
        title="Your Shopping Cart is Empty"
        description="Your cart is waiting for something beautiful. Explore our luxury collections today."
        buttonText="Explore Collection"
        (action)="navigateToShop()">
      </app-empty-state>

      <!-- Cart Content -->
      <div *ngIf="!isLoading && cart && cart.items && cart.items.length > 0" class="row g-4">
        <!-- Cart Items List -->
        <div class="col-lg-8">
          <div class="glass-card p-4">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom">
              <h5 class="fw-bold text-dark mb-0">Cart Items ({{ cart.items.length }})</h5>
              <button class="btn btn-link text-danger text-decoration-none p-0 fs-7" (click)="openClearModal()">
                <i class="fa fa-trash-alt me-1"></i> Clear Cart
              </button>
            </div>

            <div *ngFor="let item of cart.items" class="d-flex flex-column flex-md-row align-items-md-center justify-content-between p-3 mb-3 rounded-3 bg-white bg-opacity-50 border">
              <div class="d-flex align-items-center gap-3 mb-3 mb-md-0">
                <img [src]="item.primaryImageUrl || 'assets/images/placeholder.jpg'" 
                     [alt]="item.productName"
                     class="rounded-3 object-fit-cover" 
                     style="width: 80px; height: 80px;"
                     (error)="onImageError($event)">
                <div>
                  <h6 class="fw-bold text-dark mb-1">{{ item.productName }}</h6>
                  <p class="text-muted fs-7 mb-1">SKU: {{ item.productSku }}</p>
                  <span class="fw-bold text-gold">₹{{ item.unitPrice | number:'1.0-0' }}</span>
                </div>
              </div>

              <div class="d-flex align-items-center justify-content-between gap-4">
                <div class="input-group" style="width: 120px;">
                  <button class="glass-button-outline btn-sm px-2" (click)="updateQuantity(item, item.quantity - 1)" [disabled]="item.quantity <= 1">-</button>
                  <input type="text" class="form-control text-center fw-bold bg-transparent border-0" [value]="item.quantity" readonly>
                  <button class="glass-button-outline btn-sm px-2" (click)="updateQuantity(item, item.quantity + 1)">+</button>
                </div>

                <div class="text-end" style="min-width: 100px;">
                  <span class="fw-bold fs-6 text-dark d-block">₹{{ item.subtotal | number:'1.0-0' }}</span>
                  <button class="btn btn-link text-danger p-0 fs-7 text-decoration-none" (click)="removeItem(item.id)">Remove</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Order Summary Side Panel -->
        <div class="col-lg-4">
          <div class="glass-card p-4">
            <h5 class="fw-bold text-dark mb-4 pb-3 border-bottom">Order Summary</h5>
            
            <div class="d-flex justify-content-between mb-3 text-secondary">
              <span>Subtotal</span>
              <span class="fw-bold text-dark">₹{{ cart.totalAmount | number:'1.0-0' }}</span>
            </div>
            
            <div class="d-flex justify-content-between mb-3 text-secondary">
              <span>Insured Shipping</span>
              <span class="text-success fw-semibold">FREE</span>
            </div>

            <hr class="my-3">

            <div class="d-flex justify-content-between mb-4">
              <span class="fw-bold fs-5 text-dark">Total Amount</span>
              <span class="fw-bold fs-4 text-gold">₹{{ cart.totalAmount | number:'1.0-0' }}</span>
            </div>

            <button class="glass-button w-100 py-3" (click)="proceedToCheckout()">
              Proceed to Checkout <i class="fa fa-arrow-right ms-2"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Confirm Clear Modal -->
    <app-confirm-dialog
      [isOpen]="showClearModal"
      title="Clear Shopping Cart"
      message="Are you sure you want to remove all luxury items from your cart?"
      confirmText="Clear Cart"
      (confirmed)="confirmClearCart()"
      (cancelled)="showClearModal = false">
    </app-confirm-dialog>
  `,
  styles: [`
    .text-gold { color: var(--primary-gold); }
    .fs-7 { font-size: 0.8rem; }
    .object-fit-cover { object-fit: cover; }
  `]
})
export class CartComponent implements OnInit {
  cart: Cart | null = null;
  isLoading = true;
  showClearModal = false;

  constructor(
    private cartService: CartService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.isLoading = true;
    this.cartService.loadCart().subscribe({
      next: c => {
        this.cart = c;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  updateQuantity(item: CartItem, newQty: number): void {
    if (newQty < 1) return;
    this.cartService.updateItem(item.id, newQty).subscribe({
      next: updated => this.cart = updated,
      error: (err) => this.toastService.error(err.error?.message || 'Quantity update failed')
    });
  }

  removeItem(itemId: number): void {
    this.cartService.removeItem(itemId).subscribe({
      next: updated => {
        this.cart = updated;
        this.toastService.info('Item removed from cart');
      },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to remove item')
    });
  }

  openClearModal(): void {
    this.showClearModal = true;
  }

  confirmClearCart(): void {
    this.showClearModal = false;
    this.cartService.clearCart().subscribe({
      next: () => {
        this.cart = null;
        this.toastService.info('Cart cleared');
      },
      error: (err) => this.toastService.error(err.error?.message || 'Failed to clear cart')
    });
  }

  proceedToCheckout(): void {
    this.cartService.validateCart().subscribe({
      next: val => {
        if (val.valid) {
          this.router.navigate(['/checkout']);
        } else {
          const errMsg = val.errors?.map(e => e.message).join(', ') || 'Cart validation issues detected';
          this.toastService.error(errMsg);
        }
      },
      error: () => this.router.navigate(['/checkout'])
    });
  }

  navigateToShop(): void {
    this.router.navigate(['/products']);
  }

  onImageError(event: any): void {
    event.target.src = 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=600&q=80';
  }
}
