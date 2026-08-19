import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ProductService } from '../../core/services/product.service';
import { CartService } from '../../core/services/cart.service';
import { ToastService } from '../../core/services/toast.service';
import { ProductCardComponent } from '../../shared/components/product-card/product-card.component';
import { Product } from '../../core/models/models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, ProductCardComponent],
  template: `
    <!-- ── Hero ── -->
    <section class="hero-section">
      <div class="container position-relative z-1">
        <div class="row align-items-center g-5">
          <div class="col-lg-6 fade-in-up">
            <span class="glass-badge mb-4 d-inline-flex">
              <i class="fa fa-star me-2"></i> Handcrafted Luxury Jewelry
            </span>
            <h1 class="display-4 fw-bold font-serif mb-4 lh-sm">
              Timeless<br>
              <span class="text-gradient">Elegance</span><br>
              Crafted For You
            </h1>
            <p class="text-secondary fs-5 fw-light mb-5" style="max-width:480px;line-height:1.8;">
              Discover certified luxury diamond &amp; gold creations, meticulously handcrafted by master artisans for life's most precious moments.
            </p>
            <div class="d-flex flex-wrap gap-3">
              <a routerLink="/products" class="glass-button py-3 px-5">
                Explore Collection <i class="fa fa-arrow-right ms-2"></i>
              </a>
              <a routerLink="/register" class="glass-button-outline py-3 px-5">
                Join LUXE Club
              </a>
            </div>
            <!-- Stats -->
            <div class="d-flex gap-5 mt-5 pt-3 border-top border-light">
              <div>
                <div class="fw-bold fs-4 font-serif text-gold">50K+</div>
                <div class="text-secondary fs-7 tracking-wide">Happy Clients</div>
              </div>
              <div>
                <div class="fw-bold fs-4 font-serif text-gold">100%</div>
                <div class="text-secondary fs-7 tracking-wide">Certified</div>
              </div>
              <div>
                <div class="fw-bold fs-4 font-serif text-gold">25Y+</div>
                <div class="text-secondary fs-7 tracking-wide">Legacy</div>
              </div>
            </div>
          </div>
          <div class="col-lg-6 text-center">
            <div class="hero-img-container position-relative d-inline-block">
              <div class="hero-img-ring"></div>
              <div class="glass-card p-3 hero-img-card">
                <img src="https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?auto=format&fit=crop&w=720&q=80"
                     alt="Luxury Diamond Jewelry" class="img-fluid rounded-3" style="max-height:440px;object-fit:cover;">
              </div>
              <!-- Floating badges -->
              <div class="floating-badge-1 glass-card px-3 py-2 d-flex align-items-center gap-2">
                <i class="fa fa-certificate text-gold"></i>
                <span class="fw-semibold" style="font-size:0.8rem;">GIA Certified</span>
              </div>
              <div class="floating-badge-2 glass-card px-3 py-2 d-flex align-items-center gap-2">
                <i class="fa fa-shield-alt text-gold"></i>
                <span class="fw-semibold" style="font-size:0.8rem;">Lifetime Warranty</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ── Categories ── -->
    <section class="py-6 position-relative">
      <div class="container">
        <div class="text-center mb-5">
          <span class="glass-badge mb-3 d-inline-flex"><i class="fa fa-gem me-2"></i>Collections</span>
          <h2 class="fw-bold font-serif display-6 text-dark">Browse Our Collections</h2>
          <p class="text-secondary mx-auto" style="max-width:480px;">Explore our curated range of handcrafted luxury jewelry pieces.</p>
        </div>
        <div class="row g-4">
          <div class="col-6 col-lg-3" *ngFor="let cat of categories; let i = index">
            <a [routerLink]="['/products']" [queryParams]="{categoryId: cat.id}" class="text-decoration-none category-card">
              <div class="glass-card p-4 text-center h-100" style="min-height:190px;">
                <div class="cat-icon-wrap mx-auto mb-3">
                  <i [class]="cat.icon"></i>
                </div>
                <h6 class="fw-bold text-dark mb-1">{{ cat.name }}</h6>
                <p class="text-secondary fs-7 mb-2">{{ cat.desc }}</p>
                <span class="text-gold fs-7 fw-semibold">Shop now <i class="fa fa-arrow-right ms-1"></i></span>
              </div>
            </a>
          </div>
        </div>
      </div>
    </section>

    <!-- ── Featured Products ── -->
    <section class="py-6" style="background:linear-gradient(135deg,rgba(201,162,39,0.04) 0%,rgba(232,217,181,0.08) 100%);border-top:1px solid rgba(201,162,39,0.1);border-bottom:1px solid rgba(201,162,39,0.1);">
      <div class="container">
        <div class="d-flex justify-content-between align-items-end mb-5 flex-wrap gap-3">
          <div>
            <span class="glass-badge mb-2 d-inline-flex"><i class="fa fa-star me-2"></i>Curated</span>
            <h2 class="fw-bold font-serif display-6 text-dark mb-1">Featured Creations</h2>
            <p class="text-secondary mb-0">Directly from our Master Craftsmen</p>
          </div>
          <a routerLink="/products" class="glass-button-outline">View All Jewelry</a>
        </div>

        <!-- Skeleton -->
        <div class="row g-4" *ngIf="isLoading">
          <div class="col-lg-3 col-md-6" *ngFor="let i of [1,2,3,4]">
            <div class="glass-card p-0 overflow-hidden" style="height:380px;">
              <div class="skeleton w-100" style="height:230px;border-radius:var(--radius-md) var(--radius-md) 0 0;"></div>
              <div class="p-3">
                <div class="skeleton mb-2" style="height:12px;width:60%;border-radius:6px;"></div>
                <div class="skeleton mb-2" style="height:16px;width:80%;border-radius:6px;"></div>
                <div class="skeleton" style="height:12px;width:40%;border-radius:6px;"></div>
              </div>
            </div>
          </div>
        </div>

        <div class="row g-4" *ngIf="!isLoading && featuredProducts.length > 0">
          <div class="col-lg-3 col-md-6" *ngFor="let product of featuredProducts">
            <app-product-card [product]="product"
              (addToCart)="onAddToCart($event)"
              (viewDetails)="onViewDetails($event)">
            </app-product-card>
          </div>
        </div>
      </div>
    </section>

    <!-- ── Why Us ── -->
    <section class="py-6">
      <div class="container">
        <div class="text-center mb-5">
          <h2 class="fw-bold font-serif display-6 text-dark">The LUXE Promise</h2>
        </div>
        <div class="row g-4">
          <div class="col-md-6 col-lg-3" *ngFor="let feat of features">
            <div class="glass-card p-4 h-100 text-center feature-card">
              <div class="feat-icon-wrap mx-auto mb-3">
                <i [class]="feat.icon"></i>
              </div>
              <h6 class="fw-bold text-dark mb-2">{{ feat.title }}</h6>
              <p class="text-secondary fs-7 mb-0">{{ feat.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ── CTA ── -->
    <section class="py-6" style="background:linear-gradient(135deg, #1c1c1c 0%, #2a2a2a 100%);">
      <div class="container text-center">
        <span class="glass-badge mb-4 d-inline-flex" style="background:rgba(201,162,39,0.2);color:var(--champagne);border-color:rgba(201,162,39,0.4);">
          <i class="fa fa-gem me-2"></i> Exclusive Access
        </span>
        <h2 class="font-serif display-5 fw-bold mb-3" style="color:#e8d9b5;">Find Your Perfect Piece</h2>
        <p class="mb-5 mx-auto" style="color:rgba(255,255,255,0.6);max-width:500px;">
          Every piece tells a story. Discover jewelry crafted to celebrate your most meaningful moments.
        </p>
        <a routerLink="/products" class="glass-button py-3 px-5">
          <i class="fa fa-gem me-2"></i> Explore Full Collection
        </a>
      </div>
    </section>
  `,
  styles: [`
    .py-6 { padding-top: 5rem; padding-bottom: 5rem; }
    .text-gold { color: var(--primary-gold); }
    .text-gradient {
      background: linear-gradient(135deg, var(--primary-gold) 0%, #e0b84a 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
    .hero-img-container { position: relative; }
    .hero-img-card { border-radius: 24px !important; }
    .hero-img-ring {
      position: absolute; inset: -24px;
      border: 1px solid rgba(201,162,39,0.2);
      border-radius: 32px;
      animation: pulse-ring 3s ease-in-out infinite;
      pointer-events: none;
    }
    @keyframes pulse-ring { 0%,100% { opacity:0.3; transform:scale(1); } 50% { opacity:0.6; transform:scale(1.02); } }
    .floating-badge-1 {
      position: absolute; bottom: 40px; left: -30px;
      border-radius: 14px !important; font-size:0.8rem;
      animation: float 3s ease-in-out infinite;
    }
    .floating-badge-2 {
      position: absolute; top: 30px; right: -24px;
      border-radius: 14px !important; font-size:0.8rem;
      animation: float 3.5s ease-in-out infinite reverse;
    }
    @keyframes float { 0%,100%{transform:translateY(0)} 50%{transform:translateY(-8px)} }
    .cat-icon-wrap {
      width: 64px; height: 64px;
      background: linear-gradient(135deg, rgba(201,162,39,0.12), rgba(201,162,39,0.06));
      border: 1px solid rgba(201,162,39,0.2);
      border-radius: 18px;
      display: flex; align-items: center; justify-content: center;
      font-size: 1.5rem; color: var(--primary-gold);
      transition: all 0.3s;
    }
    .category-card:hover .cat-icon-wrap { background: var(--primary-gold); color: #fff; border-color:var(--primary-gold); }
    .feat-icon-wrap {
      width: 60px; height: 60px;
      background: linear-gradient(135deg, rgba(201,162,39,0.1), rgba(201,162,39,0.04));
      border: 1px solid rgba(201,162,39,0.15);
      border-radius: 16px;
      display: flex; align-items: center; justify-content: center;
      font-size: 1.4rem; color: var(--primary-gold);
    }
    .feature-card { transition: all 0.3s; }
    .feature-card:hover { transform: translateY(-4px); }
    @media(max-width:768px) {
      .floating-badge-1, .floating-badge-2 { display:none !important; }
      .hero-img-ring { display:none; }
    }
  `]
})
export class HomeComponent implements OnInit {
  featuredProducts: Product[] = [];
  isLoading = true;

  categories = [
    { id: 1, name: 'Diamond Rings',  icon: 'fa fa-ring',      desc: 'Solitaires & Wedding Bands' },
    { id: 2, name: 'Necklaces',      icon: 'fa fa-gem',       desc: 'Royal Gold & Pendant Chains' },
    { id: 3, name: 'Bracelets',      icon: 'fa fa-circle',    desc: 'Tennis & Bangle Collections' },
    { id: 4, name: 'Earrings',       icon: 'fa fa-star',      desc: 'Studs & Drop Earrings' }
  ];

  features = [
    { icon: 'fa fa-certificate', title: 'Authentic Jewelry',    desc: '100% Certified Gold & Diamonds with GIA grading' },
    { icon: 'fa fa-shield-alt',  title: 'Secure Payments',      desc: 'Encrypted end-to-end gateway processing' },
    { icon: 'fa fa-gem',         title: 'Master Craftsmanship', desc: 'Handcrafted by 3rd generation artisans' },
    { icon: 'fa fa-truck',       title: 'Insured Shipping',     desc: 'Free insured express delivery nationwide' }
  ];

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.productService.getProducts({ size: 8 }).subscribe({
      next: res => { this.featuredProducts = res.content || []; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  onAddToCart(product: Product): void {
    this.cartService.addItem(product.id, 1).subscribe({
      next: () => this.toastService.success(`${product.name} added to cart!`),
      error: err => this.toastService.error(err.error?.message || 'Failed to add to cart')
    });
  }

  onViewDetails(id: number): void {
    this.router.navigate(['/products', id]);
  }
}
