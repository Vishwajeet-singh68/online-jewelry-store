import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { OrderService } from '../../core/services/order.service';
import { Order } from '../../core/models/models';

@Component({
  selector: 'app-order-success',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container py-5 text-center">
      <div class="glass-card p-5 max-w-lg mx-auto">
        <div class="mb-4 text-success display-3">
          <i class="fa fa-check-circle"></i>
        </div>
        <h2 class="fw-bold font-serif text-dark mb-2">Order Confirmed</h2>
        <p class="text-secondary mb-4">Thank you for your purchase. Your order has been registered with our Master Craftsmen.</p>

        <div *ngIf="order" class="glass-panel text-start mb-4">
          <div class="d-flex justify-content-between mb-2">
            <span class="text-muted">Order Number:</span>
            <span class="fw-bold text-dark">{{ order.orderNumber }}</span>
          </div>
          <div class="d-flex justify-content-between mb-2">
            <span class="text-muted">Total Amount:</span>
            <span class="fw-bold text-gold">₹{{ order.totalAmount | number:'1.0-0' }}</span>
          </div>
          <div class="d-flex justify-content-between">
            <span class="text-muted">Status:</span>
            <span class="glass-badge glass-badge-success">{{ order.status }}</span>
          </div>
        </div>

        <div class="d-flex gap-3 justify-content-center">
          <a *ngIf="order" [routerLink]="['/orders', order.id]" class="glass-button">View Order Details</a>
          <a routerLink="/products" class="glass-button-outline">Continue Shopping</a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .text-gold { color: var(--primary-gold); }
    .max-w-lg { max-width: 32rem; }
  `]
})
export class OrderSuccessComponent implements OnInit {
  order: Order | null = null;

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
    private router: Router
  ) {
    const nav = this.router.getCurrentNavigation();
    if (nav?.extras.state && nav.extras.state['order']) {
      this.order = nav.extras.state['order'];
    }
  }

  ngOnInit(): void {
    if (!this.order) {
      this.route.params.subscribe(params => {
        const orderId = +params['orderId'];
        if (orderId) {
          this.orderService.getOrderById(orderId).subscribe({
            next: o => this.order = o,
            error: () => {}
          });
        }
      });
    }
  }
}
