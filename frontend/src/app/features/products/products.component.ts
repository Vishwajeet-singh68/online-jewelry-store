import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../core/services/product.service';
import { CartService } from '../../core/services/cart.service';
import { ToastService } from '../../core/services/toast.service';
import { ProductCardComponent } from '../../shared/components/product-card/product-card.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { ErrorStateComponent } from '../../shared/components/error-state/error-state.component';
import { Product, Category, PageResponse } from '../../core/models/models';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ProductCardComponent, EmptyStateComponent, ErrorStateComponent],
  template: `
    <!-- Page Header -->
    <div class="page-title-strip">
      <div class="container">
        <span class="glass-badge mb-2 d-inline-flex"><i class="fa fa-gem me-2"></i>Catalog</span>
        <h1 class="fw-bold font-serif display-6 text-dark mb-0">Our Jewelry Collection</h1>
      </div>
    </div>

    <div class="container pb-6">
      <!-- Filters -->
      <div class="glass-card p-4 mb-4">
        <div class="row g-3 align-items-center">
          <div class="col-12 col-md-5">
            <div class="position-relative">
              <i class="fa fa-search position-absolute text-secondary" style="top:50%;left:14px;transform:translateY(-50%);"></i>
              <input type="text" class="glass-input ps-5" placeholder="Search rings, necklaces, bracelets…"
                     [(ngModel)]="searchQuery" (ngModelChange)="onSearchChange()">
            </div>
          </div>
          <div class="col-6 col-md-4">
            <select class="glass-input" [(ngModel)]="selectedCategoryId" (change)="loadProducts()">
              <option [ngValue]="null">All Categories</option>
              <option *ngFor="let c of categories" [value]="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div class="col-6 col-md-3">
            <select class="glass-input" [(ngModel)]="sortBy" (change)="loadProducts()">
              <option value="createdAt">Newest First</option>
              <option value="price">Price: Low to High</option>
              <option value="name">Name A–Z</option>
            </select>
          </div>
        </div>
      </div>

      <!-- Results info -->
      <div *ngIf="pageData && !isLoading" class="d-flex align-items-center justify-content-between mb-4 flex-wrap gap-2">
        <p class="text-secondary fs-7 mb-0">
          Showing <strong>{{ products.length }}</strong> of <strong>{{ pageData.totalElements }}</strong> pieces
        </p>
        <button *ngIf="selectedCategoryId || searchQuery" class="glass-button-outline btn-sm" (click)="resetFilters()">
          <i class="fa fa-times me-1"></i> Clear Filters
        </button>
      </div>

      <!-- Skeleton -->
      <div *ngIf="isLoading" class="row g-4">
        <div class="col-lg-3 col-md-6" *ngFor="let i of [1,2,3,4,5,6,7,8]">
          <div class="glass-card overflow-hidden" style="height:350px;">
            <div class="skeleton w-100" style="height:230px;"></div>
            <div class="p-3">
              <div class="skeleton mb-2" style="height:10px;width:50%;border-radius:5px;"></div>
              <div class="skeleton mb-2" style="height:14px;width:80%;border-radius:5px;"></div>
              <div class="skeleton" style="height:10px;width:35%;border-radius:5px;"></div>
            </div>
          </div>
        </div>
      </div>

      <app-error-state *ngIf="hasError && !isLoading" (retry)="loadProducts()"></app-error-state>
      <app-empty-state *ngIf="!isLoading && !hasError && products.length===0"
        title="No Pieces Found" description="No jewelry matches your current search or filters."
        buttonText="Browse All" (action)="resetFilters()">
      </app-empty-state>

      <!-- Products Grid -->
      <div *ngIf="!isLoading && !hasError && products.length > 0" class="row g-4 mb-4">
        <div class="col-lg-3 col-md-6" *ngFor="let product of products">
          <app-product-card [product]="product"
            (addToCart)="onAddToCart($event)"
            (viewDetails)="onViewDetails($event)">
          </app-product-card>
        </div>
      </div>

      <!-- Pagination -->
      <div *ngIf="pageData && pageData.totalPages > 1" class="d-flex justify-content-center align-items-center gap-3 mt-4">
        <button class="glass-button-outline btn-sm" [disabled]="pageData.pageNumber === 0" (click)="changePage(pageData.pageNumber - 1)">
          <i class="fa fa-chevron-left me-1"></i> Prev
        </button>
        <div class="d-flex gap-1">
          <span *ngFor="let p of getPages()" >
            <button class="page-btn" [class.active]="p === pageData.pageNumber" (click)="changePage(p)">{{ p+1 }}</button>
          </span>
        </div>
        <button class="glass-button-outline btn-sm" [disabled]="pageData.last" (click)="changePage(pageData.pageNumber + 1)">
          Next <i class="fa fa-chevron-right ms-1"></i>
        </button>
      </div>
    </div>
  `,
  styles: [`
    .pb-6 { padding-bottom: 5rem; }
    .page-btn {
      width:36px;height:36px;border-radius:8px;
      background:rgba(255,255,255,0.5);
      border:1px solid var(--glass-border);
      font-weight:600;font-size:0.85rem;
      cursor:pointer;transition:all 0.2s;
    }
    .page-btn.active { background:var(--primary-gold);color:#fff;border-color:var(--primary-gold); }
    .page-btn:hover:not(.active) { background:rgba(201,162,39,0.1);border-color:var(--primary-gold); }
    .fs-7 { font-size:0.82rem; }
  `]
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = [];
  pageData: PageResponse<Product> | null = null;
  isLoading = true;
  hasError = false;
  searchQuery = '';
  selectedCategoryId: number | null = null;
  sortBy = 'createdAt';
  currentPage = 0;
  private searchTimeout: any;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private toastService: ToastService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.productService.getCategories().subscribe({ next: c => this.categories = c, error: ()=>{} });
    this.route.queryParams.subscribe(params => {
      if (params['categoryId']) this.selectedCategoryId = +params['categoryId'];
      this.loadProducts();
    });
  }

  loadProducts(): void {
    this.isLoading = true; this.hasError = false;
    this.productService.getProducts({
      page: this.currentPage, size: 12, sortBy: this.sortBy,
      categoryId: this.selectedCategoryId || undefined,
      search: this.searchQuery || undefined
    }).subscribe({
      next: res => { this.products = res.content || []; this.pageData = res; this.isLoading = false; },
      error: () => { this.isLoading = false; this.hasError = true; }
    });
  }

  onSearchChange(): void {
    clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => { this.currentPage = 0; this.loadProducts(); }, 420);
  }

  resetFilters(): void {
    this.searchQuery = ''; this.selectedCategoryId = null; this.currentPage = 0; this.loadProducts();
  }

  changePage(p: number): void { this.currentPage = p; this.loadProducts(); window.scrollTo(0,0); }

  getPages(): number[] {
    if (!this.pageData) return [];
    const total = this.pageData.totalPages;
    const cur = this.pageData.pageNumber;
    const pages: number[] = [];
    for (let i = Math.max(0, cur-2); i <= Math.min(total-1, cur+2); i++) pages.push(i);
    return pages;
  }

  onAddToCart(product: Product): void {
    this.cartService.addItem(product.id, 1).subscribe({
      next: () => this.toastService.success(`${product.name} added to cart!`),
      error: err => this.toastService.error(err.error?.message || 'Failed to add to cart')
    });
  }

  onViewDetails(id: number): void { this.router.navigate(['/products', id]); }
}
