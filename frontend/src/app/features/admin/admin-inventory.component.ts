import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventoryService } from '../../core/services/inventory.service';
import { ToastService } from '../../core/services/toast.service';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';
import { Inventory } from '../../core/models/models';

@Component({
  selector: 'app-admin-inventory',
  standalone: true,
  imports: [CommonModule, FormsModule, ConfirmDialogComponent],
  template: `
    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold font-serif text-dark mb-0">Inventory Stock Management</h3>
        <button class="glass-button-outline btn-sm" (click)="loadInventory()">
          <i class="fa fa-sync me-1"></i> Refresh Stock
        </button>
      </div>

      <div *ngIf="isLoading" class="glass-card p-5 text-center">
        <i class="fa fa-spinner fa-spin text-gold fs-1 mb-3"></i>
        <p class="text-muted">Loading live inventory details...</p>
      </div>

      <div *ngIf="!isLoading && inventoryList.length > 0" class="glass-card p-4">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light text-uppercase fs-7">
              <tr>
                <th>Product ID</th>
                <th>SKU</th>
                <th>Available Stock</th>
                <th>Reserved</th>
                <th>Stock Status</th>
                <th class="text-end">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let item of inventoryList">
                <td class="fw-bold">#{{ item.productId }}</td>
                <td><span class="badge bg-secondary bg-opacity-25 text-dark">{{ item.sku }}</span></td>
                <td class="fw-bold fs-6 text-dark">{{ item.availableQuantity }}</td>
                <td class="text-muted">{{ item.reservedQuantity }}</td>
                <td>
                  <span class="glass-badge" [ngClass]="{
                    'glass-badge-success': item.stockStatus === 'IN_STOCK',
                    'glass-badge-warning': item.stockStatus === 'LOW_STOCK',
                    'glass-badge-danger': item.stockStatus === 'OUT_OF_STOCK'
                  }">
                    {{ item.stockStatus }}
                  </span>
                </td>
                <td class="text-end">
                  <button class="glass-button-outline btn-sm py-1 px-3 me-2" (click)="openAdjustModal(item, 10)">
                    +10 Restock
                  </button>
                  <button class="glass-button btn-sm py-1 px-3" (click)="openAdjustModal(item, -5)">
                    -5 Adjust
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Confirm Adjustment Modal -->
    <app-confirm-dialog
      [isOpen]="showAdjustModal"
      title="Adjust Inventory Stock"
      [message]="'Confirm adjusting stock for SKU ' + (selectedItem?.sku || '') + ' by ' + adjustmentQty + ' units?'"
      confirmText="Apply Stock Adjustment"
      (confirmed)="applyStockAdjustment()"
      (cancelled)="showAdjustModal = false">
    </app-confirm-dialog>
  `,
  styles: [`
    .fs-7 { font-size: 0.8rem; }
    .table-hover tbody tr:hover { background-color: rgba(201, 162, 39, 0.05); }
  `]
})
export class AdminInventoryComponent implements OnInit {
  inventoryList: Inventory[] = [];
  isLoading = true;

  selectedItem: Inventory | null = null;
  adjustmentQty = 0;
  showAdjustModal = false;

  constructor(
    private inventoryService: InventoryService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadInventory();
  }

  loadInventory(): void {
    this.isLoading = true;
    this.inventoryService.getAdminInventory().subscribe({
      next: invs => {
        this.inventoryList = invs || [];
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  openAdjustModal(item: Inventory, amount: number): void {
    this.selectedItem = item;
    this.adjustmentQty = amount;
    this.showAdjustModal = true;
  }

  applyStockAdjustment(): void {
    if (!this.selectedItem) return;
    this.showAdjustModal = false;

    this.inventoryService.adjustStock(this.selectedItem.productId, {
      quantity: this.adjustmentQty,
      reason: 'RESTOCK'
    }).subscribe({
      next: updated => {
        this.toastService.success(`Stock updated for ${updated.sku}`);
        this.loadInventory();
      },
      error: err => this.toastService.error(err.error?.message || 'Stock adjustment failed')
    });
  }
}
