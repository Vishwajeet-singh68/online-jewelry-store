import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../core/services/product.service';
import { InventoryService } from '../../core/services/inventory.service';
import { OrderService } from '../../core/services/order.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <span class="glass-badge mb-1 d-inline-block">Admin Portal</span>
          <h2 class="fw-bold font-serif text-dark mb-0">Jewelry Store Management</h2>
        </div>
      </div>

      <!-- Quick Metrics Cards -->
      <div class="row g-4 mb-5">
        <div class="col-md-4">
          <div class="glass-card p-4 d-flex align-items-center justify-content-between">
            <div>
              <span class="text-muted fs-7 font-bold text-uppercase">Total Catalog Products</span>
              <h3 class="fw-bold text-dark mb-0 mt-1">{{ totalProducts }}</h3>
            </div>
            <i class="fa fa-gem text-gold fs-1"></i>
          </div>
        </div>

        <div class="col-md-4">
          <div class="glass-card p-4 d-flex align-items-center justify-content-between">
            <div>
              <span class="text-muted fs-7 font-bold text-uppercase">Low Stock Alerts</span>
              <h3 class="fw-bold text-warning mb-0 mt-1">{{ lowStockCount }}</h3>
            </div>
            <i class="fa fa-exclamation-triangle text-warning fs-1"></i>
          </div>
        </div>

        <div class="col-md-4">
          <div class="glass-card p-4 d-flex align-items-center justify-content-between">
            <div>
              <span class="text-muted fs-7 font-bold text-uppercase">Customer Orders</span>
              <h3 class="fw-bold text-dark mb-0 mt-1">{{ totalOrders }}</h3>
            </div>
            <i class="fa fa-box text-gold fs-1"></i>
          </div>
        </div>
      </div>

      <!-- Admin Navigation Panels -->
      <div class="row g-4">
        <div class="col-md-6">
          <div class="glass-card p-4 h-100 d-flex flex-column justify-content-between">
            <div>
              <i class="fa fa-boxes text-gold fs-2 mb-3"></i>
              <h5 class="fw-bold text-dark mb-2">Inventory Stock & Restock</h5>
              <p class="text-secondary fs-7">
                Inspect live inventory levels across products, perform quantity adjustments, and view stock status.
              </p>
            </div>
            <a routerLink="/admin/inventory" class="glass-button w-100 text-center mt-3">Manage Inventory</a>
          </div>
        </div>

        <div class="col-md-6">
          <div class="glass-card p-4 h-100 d-flex flex-column justify-content-between">
            <div>
              <i class="fa fa-shopping-cart text-gold fs-2 mb-3"></i>
              <h5 class="fw-bold text-dark mb-2">Customer Order Processing</h5>
              <p class="text-secondary fs-7">
                Review incoming orders, inspect shipping details, and advance order states (Processing, Shipped, Delivered).
              </p>
            </div>
            <a routerLink="/admin/orders" class="glass-button-outline w-100 text-center mt-3">Manage Orders</a>
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
export class AdminDashboardComponent implements OnInit {
  totalProducts = 0;
  lowStockCount = 0;
  totalOrders = 0;

  constructor(
    private productService: ProductService,
    private inventoryService: InventoryService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.productService.getProducts({ size: 1 }).subscribe({
      next: res => this.totalProducts = res.totalElements || 0,
      error: () => {}
    });

    this.inventoryService.getAdminInventory().subscribe({
      next: invs => this.lowStockCount = invs.filter(i => i.stockStatus === 'LOW_STOCK' || i.stockStatus === 'OUT_OF_STOCK').length,
      error: () => {}
    });

    this.orderService.getAdminOrders(0, 1).subscribe({
      next: res => this.totalOrders = res.totalElements || 0,
      error: () => {}
    });
  }
}
