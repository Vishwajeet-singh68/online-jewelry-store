import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { OrderService } from '../../core/services/order.service';
import { ToastService } from '../../core/services/toast.service';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';
import { Order } from '../../core/models/models';

@Component({
  selector: 'app-order-details',
  standalone: true,
  imports: [CommonModule, RouterModule, ConfirmDialogComponent],
  template: `
    <div class="container py-4">
      <div *ngIf="isLoading" class="glass-card p-5 text-center">
        <i class="fa fa-spinner fa-spin text-gold fs-1 mb-3"></i>
        <p class="text-muted">Loading order details...</p>
      </div>

      <div *ngIf="!isLoading && order" class="glass-card p-4 p-md-5">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center border-bottom pb-4 mb-4">
          <div>
            <span class="text-muted fs-7 text-uppercase tracking-wider">Order Details</span>
            <h3 class="fw-bold font-serif text-dark mb-1">#{{ order.orderNumber }}</h3>
            <span class="text-muted fs-7">Placed on {{ order.createdAt | date:'medium' }}</span>
          </div>

          <div class="d-flex align-items-center gap-3 mt-3 mt-md-0">
            <span class="glass-badge" [ngClass]="{
              'glass-badge-success': order.status === 'DELIVERED' || order.status === 'CONFIRMED',
              'glass-badge-warning': order.status === 'PENDING' || order.status === 'PROCESSING',
              'glass-badge-danger': order.status === 'CANCELLED'
            }">
              {{ order.status }}
            </span>

            <button *ngIf="canCancel(order)" class="glass-button-outline btn-sm text-danger border-danger" (click)="showCancelModal = true">
              Cancel Order
            </button>
          </div>
        </div>

        <div class="row g-4 mb-4">
          <!-- Items List -->
          <div class="col-lg-8">
            <h5 class="fw-bold text-dark mb-3">Purchased Items</h5>
            <div *ngFor="let item of order.items" class="d-flex justify-content-between align-items-center p-3 mb-2 rounded-3 bg-white bg-opacity-50 border">
              <div>
                <h6 class="fw-bold text-dark mb-1">{{ item.productName }}</h6>
                <span class="text-muted fs-7">SKU: {{ item.productSku }} | Qty: {{ item.quantity }}</span>
              </div>
              <span class="fw-bold text-gold">₹{{ item.subtotal | number:'1.0-0' }}</span>
            </div>
          </div>

          <!-- Shipping Info -->
          <div class="col-lg-4">
            <div class="glass-panel h-100">
              <h5 class="fw-bold text-dark mb-3"><i class="fa fa-truck me-2 text-gold"></i> Shipping Address</h5>
              <div *ngIf="order.shippingAddress" class="text-secondary fs-7">
                <p class="fw-bold text-dark mb-1">{{ order.shippingAddress.fullName }}</p>
                <p class="mb-1">{{ order.shippingAddress.addressLine1 }}</p>
                <p *ngIf="order.shippingAddress.addressLine2" class="mb-1">{{ order.shippingAddress.addressLine2 }}</p>
                <p class="mb-1">{{ order.shippingAddress.city }}, {{ order.shippingAddress.state }} - {{ order.shippingAddress.postalCode }}</p>
                <p class="mb-1">{{ order.shippingAddress.country }}</p>
                <p class="mb-0"><i class="fa fa-phone me-1"></i> {{ order.shippingAddress.phoneNumber }}</p>
              </div>
            </div>
          </div>
        </div>

        <div class="pt-3 border-top d-flex justify-content-between align-items-center">
          <a routerLink="/orders" class="glass-button-outline btn-sm"><i class="fa fa-arrow-left me-2"></i> Back to Orders</a>
          <div>
            <span class="text-muted me-2">Grand Total:</span>
            <span class="fw-bold fs-4 text-gold">₹{{ order.totalAmount | number:'1.0-0' }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Confirm Cancel Modal -->
    <app-confirm-dialog
      [isOpen]="showCancelModal"
      title="Cancel Order"
      message="Are you sure you want to cancel this order? Stock will be released back to inventory."
      confirmText="Cancel Order"
      (confirmed)="confirmCancelOrder()"
      (cancelled)="showCancelModal = false">
    </app-confirm-dialog>
  `,
  styles: [`
    .text-gold { color: var(--primary-gold); }
    .fs-7 { font-size: 0.85rem; }
    .tracking-wider { letter-spacing: 1px; }
  `]
})
export class OrderDetailsComponent implements OnInit {
  order: Order | null = null;
  isLoading = true;
  showCancelModal = false;

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = +params['id'];
      if (id) {
        this.loadOrder(id);
      }
    });
  }

  loadOrder(id: number): void {
    this.isLoading = true;
    this.orderService.getOrderById(id).subscribe({
      next: o => {
        this.order = o;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  canCancel(order: Order): boolean {
    return order.status === 'PENDING' || order.status === 'CONFIRMED' || order.status === 'PROCESSING';
  }

  confirmCancelOrder(): void {
    if (!this.order) return;
    this.showCancelModal = false;
    this.orderService.cancelOrder(this.order.id).subscribe({
      next: updated => {
        this.order = updated;
        this.toastService.success('Order cancelled successfully');
      },
      error: err => this.toastService.error(err.error?.message || 'Failed to cancel order')
    });
  }
}
