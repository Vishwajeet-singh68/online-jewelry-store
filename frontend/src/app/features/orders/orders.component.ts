import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { OrderService } from '../../core/services/order.service';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { Order, PageResponse } from '../../core/models/models';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, RouterModule, EmptyStateComponent],
  template: `
    <div class="container py-4">
      <h2 class="fw-bold font-serif text-dark mb-4">My Orders History</h2>

      <!-- Loading -->
      <div *ngIf="isLoading" class="glass-card p-5 text-center">
        <i class="fa fa-spinner fa-spin text-gold fs-1 mb-3"></i>
        <p class="text-muted">Loading your past orders...</p>
      </div>

      <!-- Empty State -->
      <app-empty-state 
        *ngIf="!isLoading && orders.length === 0"
        icon="fa fa-box-open"
        title="No Orders Placed Yet"
        description="You have not placed any luxury jewelry orders yet."
        buttonText="Start Shopping"
        (action)="navigateToShop()">
      </app-empty-state>

      <!-- Orders List -->
      <div *ngIf="!isLoading && orders.length > 0" class="row g-4">
        <div class="col-12" *ngFor="let order of orders">
          <div class="glass-card p-4">
            <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center border-bottom pb-3 mb-3">
              <div>
                <h5 class="fw-bold text-dark mb-1">Order #{{ order.orderNumber }}</h5>
                <span class="text-muted fs-7">Placed on {{ order.createdAt | date:'mediumDate' }}</span>
              </div>
              <div class="d-flex align-items-center gap-3 mt-2 mt-md-0">
                <span class="glass-badge" [ngClass]="{
                  'glass-badge-success': order.status === 'DELIVERED' || order.status === 'CONFIRMED',
                  'glass-badge-warning': order.status === 'PENDING' || order.status === 'PROCESSING',
                  'glass-badge-danger': order.status === 'CANCELLED'
                }">
                  {{ order.status }}
                </span>
                <a [routerLink]="['/orders', order.id]" class="glass-button-outline btn-sm">View Details</a>
              </div>
            </div>

            <div class="row align-items-center">
              <div class="col-md-8">
                <p class="text-secondary fs-7 mb-0">
                  <i class="fa fa-box me-1 text-gold"></i> Items: 
                  <span class="fw-semibold text-dark">{{ getItemSummary(order) }}</span>
                </p>
              </div>
              <div class="col-md-4 text-md-end mt-2 mt-md-0">
                <span class="text-muted fs-7 me-2">Total:</span>
                <span class="fw-bold fs-5 text-gold">₹{{ order.totalAmount | number:'1.0-0' }}</span>
              </div>
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
export class OrdersComponent implements OnInit {
  orders: Order[] = [];
  isLoading = true;

  constructor(
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.orderService.getUserOrders().subscribe({
      next: res => {
        this.orders = res.content || [];
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  getItemSummary(order: Order): string {
    if (!order.items || order.items.length === 0) return 'No items';
    return order.items.map(i => `${i.productName} (x${i.quantity})`).join(', ');
  }

  navigateToShop(): void {
    this.router.navigate(['/products']);
  }
}
