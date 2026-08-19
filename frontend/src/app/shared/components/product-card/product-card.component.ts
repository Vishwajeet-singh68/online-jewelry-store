import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="product-card-wrap glass-card p-0 overflow-hidden h-100 d-flex flex-column">
      <!-- Image -->
      <div class="product-card-img-wrap position-relative">
        <img [src]="product.primaryImageUrl || fallbackImg"
             [alt]="product.name"
             (error)="onImgErr($event)"
             class="w-100 h-100" style="object-fit:cover;transition:transform 0.5s;">
        <!-- Category chip -->
        <span class="position-absolute top-0 start-0 m-2 glass-badge" style="font-size:0.65rem;">
          {{ product.categoryName }}
        </span>
        <!-- Discount badge -->
        <span *ngIf="product.discountPrice" class="position-absolute top-0 end-0 m-2 glass-badge glass-badge-danger">
          SALE
        </span>
        <!-- Quick actions overlay -->
        <div class="product-overlay d-flex align-items-center justify-content-center gap-2">
          <button class="overlay-btn" (click)="viewDetails.emit(product.id)" title="View Details">
            <i class="fa fa-eye"></i>
          </button>
          <button class="overlay-btn overlay-btn-primary" (click)="addToCart.emit(product)" title="Add to Cart">
            <i class="fa fa-shopping-bag"></i>
          </button>
        </div>
      </div>

      <!-- Body -->
      <div class="p-3 d-flex flex-column flex-grow-1">
        <p class="text-secondary fs-8 tracking-wide text-uppercase mb-1">{{ product.sku }}</p>
        <h6 class="fw-bold text-dark mb-0 text-truncate" style="font-size:0.95rem;">{{ product.name }}</h6>

        <div class="mt-auto pt-3 d-flex align-items-center justify-content-between">
          <div>
            <span class="fw-bold fs-5 text-gold">₹{{ product.finalPrice | number:'1.0-0' }}</span>
            <span *ngIf="product.discountPrice" class="text-secondary text-decoration-line-through ms-2 fs-7">
              ₹{{ product.price | number:'1.0-0' }}
            </span>
          </div>
          <button class="add-btn" (click)="addToCart.emit(product)">
            <i class="fa fa-plus"></i>
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .product-card-wrap { transition: all 0.3s cubic-bezier(0.4,0,0.2,1); }
    .product-card-wrap:hover { transform:translateY(-6px); box-shadow:0 20px 50px rgba(201,162,39,0.18) !important; border-color:var(--primary-gold) !important; }
    .product-card-img-wrap { height:230px; background:#f5efe1; overflow:hidden; }
    .product-card-wrap:hover img { transform:scale(1.07); }
    .product-overlay {
      position:absolute; inset:0;
      background:rgba(28,28,28,0.4);
      backdrop-filter:blur(4px);
      opacity:0; transition:opacity 0.3s;
    }
    .product-card-wrap:hover .product-overlay { opacity:1; }
    .overlay-btn {
      width:42px;height:42px;
      background:rgba(255,255,255,0.85);
      border:none; border-radius:50%;
      display:flex;align-items:center;justify-content:center;
      color:#1c1c1c; font-size:0.9rem; cursor:pointer;
      transition:all 0.2s; transform:translateY(8px);
    }
    .product-card-wrap:hover .overlay-btn { transform:translateY(0); }
    .overlay-btn-primary { background:var(--primary-gold); color:#fff; }
    .overlay-btn:hover { transform:scale(1.1) translateY(0) !important; }
    .add-btn {
      width:36px;height:36px;
      background:linear-gradient(135deg,var(--primary-gold),#e0b84a);
      border:none; border-radius:10px; color:#fff;
      display:flex;align-items:center;justify-content:center;
      cursor:pointer; font-size:0.85rem;
      transition:all 0.2s; box-shadow:0 4px 12px rgba(201,162,39,0.3);
    }
    .add-btn:hover { transform:scale(1.1); box-shadow:0 6px 18px rgba(201,162,39,0.45); }
    .text-gold { color:var(--primary-gold); }
    .fs-7 { font-size:0.82rem; }
    .fs-8 { font-size:0.72rem; }
  `]
})
export class ProductCardComponent {
  @Input() product!: any;
  @Output() addToCart = new EventEmitter<any>();
  @Output() viewDetails = new EventEmitter<number>();

  fallbackImg = 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=600&q=80';

  onImgErr(e: any) { e.target.src = this.fallbackImg; }
}
