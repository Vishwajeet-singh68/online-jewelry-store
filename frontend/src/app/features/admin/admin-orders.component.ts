import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../core/services/order.service';
import { ToastService } from '../../core/services/toast.service';
import { Order } from '../../core/models/models';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold font-serif text-dark mb-0">Customer Orders Processing</h3>
        <button class="glass-button-outline btn-sm" (click)="loadOrders()">
          <i class="fa fa-sync me-1"></i> Refresh Orders
        </button>
      </div>

      <div *ngIf="isLoading" class="glass-card p-5 text-center">
        <i class="fa fa-spinner fa-spin text-gold fs-1 mb-3"></i>
        <p class="text-muted">Loading customer orders...</p>
      </div>

      <div *ngIf="!isLoading && orders.length > 0" class="glass-card p-4">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light text-uppercase fs-7">
              <tr>
                <th>Order Number</th>
                <th>Customer ID</th>
                <th>Date</th>
                <th>Amount</th>
                <th>Current Status</th>
                <th class="text-end">Update Status</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let order of orders">
                <td class="fw-bold">#{{ order.orderNumber }}</td>
                <td>User #{{ order.userId }}</td>
                <td class="fs-7 text-muted">{{ order.createdAt | date:'short' }}</td>
                <td class="fw-bold text-gold">₹{{ order.totalAmount | number:'1.0-0' }}</td>
                <td>
                  <span class="glass-badge" [ngClass]="{
                    'glass-badge-success': order.status === 'DELIVERED' || order.status === 'CONFIRMED',
                    'glass-badge-warning': order.status === 'PENDING' || order.status === 'PROCESSING',
                    'glass-badge-danger': order.status === 'CANCELLED'
                  }">
                    {{ order.status }}
                  </span>
                </td>
                <td class="text-end">
                  <div class="d-inline-flex gap-2">
                    <button *ngIf="order.status === 'CONFIRMED' || order.status === 'PENDING'" 
                            class="glass-button-outline btn-sm py-1"
                            (click)="updateStatus(order, 'PROCESSING')">
                      Process
                    </button>
                    <button *ngIf="order.status === 'PROCESSING'" 
                            class="glass-button btn-sm py-1"
                            (click)="updateStatus(order, 'SHIPPED')">
                      Ship Order
                    </button>
                    <button *ngIf="order.status === 'SHIPPED'" 
                            class="glass-button btn-sm py-1"
                            (click)="updateStatus(order, 'DELIVERED')">
                      Mark Delivered
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .text-gold { color: var(--primary-gold); }
    .fs-7 { font-size: 0.8rem; }
  `]
})
export class AdminOrdersComponent implements OnInit {
  orders: Order[] = [];
  isLoading = true;

  constructor(
    private orderService: OrderService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.isLoading = true;
    this.orderService.getAdminOrders(0, 50).subscribe({
      next: res => {
        this.orders = res.content || [];
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  updateStatus(order: Order, newStatus: string): void {
    this.orderService.updateOrderStatus(order.id, newStatus).subscribe({
      next: updated => {
        this.toastService.success(`Order #${updated.orderNumber} status changed to ${newStatus}`);
        this.loadOrders();
      },
      error: err => this.toastService.error(err.error?.message || 'Status update failed')
    });
  }
}
