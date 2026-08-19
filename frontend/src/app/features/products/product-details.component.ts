import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../core/services/product.service';
import { InventoryService } from '../../core/services/inventory.service';
import { CartService } from '../../core/services/cart.service';
import { ToastService } from '../../core/services/toast.service';
import { ErrorStateComponent } from '../../shared/components/error-state/error-state.component';
import { Product, Inventory } from '../../core/models/models';

@Component({
  selector: 'app-product-details',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ErrorStateComponent],
  template: `
    <div class="container py-4">
      <div *ngIf="isLoading" class="glass-card p-5 text-center">
        <i class="fa fa-spinner fa-spin text-gold fs-1 mb-3"></i>
        <p class="text-muted">Retrieving luxury piece details...</p>
      </div>

      <app-error-state *ngIf="hasError && !isLoading" (retry)="loadProductDetails()"></app-error-state>

      <div *ngIf="product && !isLoading" class="glass-card p-4 p-md-5">
        <div class="row g-5">
          <!-- Main Product Image -->
          <div class="col-lg-6">
            <div class="rounded-4 overflow-hidden border shadow-sm bg-white text-center p-3 mb-3">
              <img [src]="selectedImage || product.primaryImageUrl || 'assets/images/placeholder.jpg'" 
                   [alt]="product.name" 
                   class="img-fluid object-fit-cover"
                   style="max-height: 450px;"
                   (error)="onImageError($event)">
            </div>
            
            <!-- Additional Images Gallery -->
            <div *ngIf="product.imageUrls && product.imageUrls.length > 1" class="d-flex gap-2 overflow-auto">
              <img *ngFor="let img of product.imageUrls" 
                   [src]="img" 
                   class="rounded-3 border cursor-pointer" 
                   style="width: 70px; height: 70px; object-fit: cover;"
                   [ngClass]="{'border-warning': selectedImage === img}"
                   (click)="selectedImage = img">
            </div>
          </div>

          <!-- Product Details Column -->
          <div class="col-lg-6 d-flex flex-column justify-content-between">
            <div>
              <span class="glass-badge mb-2 d-inline-block">{{ product.categoryName }}</span>
              <h2 class="display-6 fw-bold font-serif text-dark mb-2">{{ product.name }}</h2>
              <p class="text-muted fs-7 mb-3">SKU: <span class="fw-semibold">{{ product.sku }}</span></p>

              <div class="d-flex align-items-center gap-3 mb-4">
                <span class="display-6 fw-bold text-gold">₹{{ product.finalPrice | number:'1.0-0' }}</span>
                <span *ngIf="product.discountPrice" class="text-muted text-decoration-line-through fs-5">
                  ₹{{ product.price | number:'1.0-0' }}
                </span>
              </div>

              <!-- Stock Availability Badge -->
              <div class="mb-4">
                <span *ngIf="inventory" [ngClass]="{
                  'glass-badge-success': inventory.stockStatus === 'IN_STOCK',
                  'glass-badge-warning': inventory.stockStatus === 'LOW_STOCK',
                  'glass-badge-danger': inventory.stockStatus === 'OUT_OF_STOCK'
                }" class="glass-badge px-3 py-2">
                  <i class="fa fa-box me-1"></i>
                  {{ inventory.stockStatus === 'IN_STOCK' ? 'In Stock (' + inventory.availableQuantity + ' available)' : 
                     inventory.stockStatus === 'LOW_STOCK' ? 'Low Stock (' + inventory.availableQuantity + ' left)' : 'Out of Stock' }}
                </span>
              </div>

              <p class="text-secondary leading-relaxed mb-4">
                {{ product.description || 'Exquisitely crafted luxury piece featuring high-grade precious materials and certified authenticity.' }}
              </p>
            </div>

            <!-- Quantity & Action Buttons -->
            <div class="pt-4 border-top">
              <div class="d-flex align-items-center gap-3 mb-4">
                <span class="fw-bold text-dark">Quantity:</span>
                <div class="input-group style-qty" style="width: 140px;">
                  <button class="glass-button-outline btn-sm px-3" (click)="decrementQty()">-</button>
                  <input type="text" class="form-control text-center fw-bold bg-transparent border-0" [value]="quantity" readonly>
                  <button class="glass-button-outline btn-sm px-3" (click)="incrementQty()">+</button>
                </div>
              </div>

              <div class="d-flex gap-3">
                <button class="glass-button flex-grow-1" [disabled]="inventory && !inventory.isAvailable" (click)="onAddToCart()">
                  <i class="fa fa-shopping-bag me-2"></i> Add to Cart
                </button>
                <button class="glass-button-outline flex-grow-1" [disabled]="inventory && !inventory.isAvailable" (click)="onBuyNow()">
                  Buy Now
                </button>
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
    .cursor-pointer { cursor: pointer; }
    .border-warning { border-color: var(--primary-gold) !important; border-width: 2px !important; }
  `]
})
export class ProductDetailsComponent implements OnInit {
  product: Product | null = null;
  inventory: Inventory | null = null;
  selectedImage: string | null = null;

  quantity = 1;
  isLoading = true;
  hasError = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private inventoryService: InventoryService,
    private cartService: CartService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = +params['id'];
      if (id) {
        this.loadProductDetails(id);
      }
    });
  }

  loadProductDetails(id?: number): void {
    const productId = id || (this.product ? this.product.id : 0);
    this.isLoading = true;
    this.hasError = false;

    this.productService.getProductById(productId).subscribe({
      next: prod => {
        this.product = prod;
        this.selectedImage = prod.primaryImageUrl || null;
        this.isLoading = false;

        // Fetch Real Inventory
        this.inventoryService.getInventoryByProductId(prod.id).subscribe({
          next: inv => this.inventory = inv,
          error: () => {}
        });
      },
      error: () => {
        this.isLoading = false;
        this.hasError = true;
      }
    });
  }

  incrementQty(): void {
    if (this.inventory && this.quantity >= this.inventory.availableQuantity) {
      this.toastService.warning(`Only ${this.inventory.availableQuantity} items available in stock`);
      return;
    }
    this.quantity++;
  }

  decrementQty(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  onAddToCart(): void {
    if (!this.product) return;
    this.cartService.addItem(this.product.id, this.quantity).subscribe({
      next: () => this.toastService.success(`Added ${this.quantity} item(s) to cart!`),
      error: (err) => this.toastService.error(err.error?.message || 'Failed to add item to cart')
    });
  }

  onBuyNow(): void {
    if (!this.product) return;
    this.cartService.addItem(this.product.id, this.quantity).subscribe({
      next: () => this.router.navigate(['/cart']),
      error: (err) => this.toastService.error(err.error?.message || 'Failed to process Buy Now')
    });
  }

  onImageError(event: any): void {
    event.target.src = 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=600&q=80';
  }
}
