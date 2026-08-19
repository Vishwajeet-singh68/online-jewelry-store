import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="luxury-navbar" [class.scrolled]="isScrolled">
      <div class="container">
        <div class="nav-inner d-flex align-items-center justify-content-between">

          <!-- Logo -->
          <a routerLink="/" class="text-decoration-none d-flex align-items-center gap-2">
            <div class="logo-icon d-flex align-items-center justify-content-center">
              <i class="fa fa-gem"></i>
            </div>
            <span class="logo-text tracking-wider text-uppercase font-serif">Luxe Jewels</span>
          </a>

          <!-- Desktop Nav -->
          <ul class="nav-links d-none d-lg-flex align-items-center list-unstyled mb-0 gap-1">
            <li><a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}" class="nav-link-luxury">Home</a></li>
            <li><a routerLink="/products" routerLinkActive="active" class="nav-link-luxury">Shop</a></li>
            <li><a routerLink="/products" [queryParams]="{categoryId:1}" class="nav-link-luxury">Rings</a></li>
            <li><a routerLink="/products" [queryParams]="{categoryId:2}" class="nav-link-luxury">Necklaces</a></li>
            <li *ngIf="authService.isAuthenticated() && isAdmin()">
              <a routerLink="/admin" routerLinkActive="active" class="nav-link-luxury" style="color:var(--primary-gold) !important;">Admin</a>
            </li>
          </ul>

          <!-- Actions -->
          <div class="d-flex align-items-center gap-2">
            <!-- Cart -->
            <a routerLink="/cart" class="nav-action-btn text-decoration-none position-relative">
              <i class="fa fa-shopping-bag"></i>
              <span *ngIf="cartService.totalItems() > 0" class="cart-count">{{ cartService.totalItems() }}</span>
            </a>

            <!-- Auth -->
            <ng-container *ngIf="authService.isAuthenticated(); else guestBlock">
              <div class="dropdown">
                <button class="glass-button-outline btn-sm d-flex align-items-center gap-2" type="button"
                        id="userMenu" data-bs-toggle="dropdown" aria-expanded="false">
                  <i class="fa fa-user-circle"></i>
                  <span class="d-none d-md-inline" style="max-width:100px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">
                    {{ authService.currentUser()?.name || 'Account' }}
                  </span>
                  <i class="fa fa-chevron-down fs-8"></i>
                </button>
                <ul class="dropdown-menu dropdown-menu-end mt-2 p-2" style="background:rgba(255,255,255,0.95);backdrop-filter:blur(20px);border:1px solid var(--glass-border);border-radius:var(--radius-md);min-width:200px;box-shadow:var(--glass-shadow-hover);">
                  <li>
                    <a class="dropdown-item rounded-2 py-2 px-3 mb-1" routerLink="/orders">
                      <i class="fa fa-box me-2 text-gold"></i> My Orders
                    </a>
                  </li>
                  <li>
                    <a class="dropdown-item rounded-2 py-2 px-3 mb-1" routerLink="/profile">
                      <i class="fa fa-id-card me-2 text-gold"></i> Profile
                    </a>
                  </li>
                  <li><hr class="dropdown-divider my-1"></li>
                  <li>
                    <button class="dropdown-item rounded-2 py-2 px-3 text-danger" (click)="onLogout()">
                      <i class="fa fa-sign-out-alt me-2"></i> Logout
                    </button>
                  </li>
                </ul>
              </div>
            </ng-container>
            <ng-template #guestBlock>
              <a routerLink="/login" class="glass-button btn-sm">
                <i class="fa fa-lock me-1"></i> Login
              </a>
            </ng-template>

            <!-- Mobile hamburger -->
            <button class="d-lg-none nav-action-btn ms-1" (click)="isMenuOpen = !isMenuOpen">
              <i [class]="isMenuOpen ? 'fa fa-times' : 'fa fa-bars'"></i>
            </button>
          </div>
        </div>

        <!-- Mobile Menu -->
        <div *ngIf="isMenuOpen" class="mobile-menu mt-2 glass-card p-3 d-lg-none fade-in">
          <ul class="list-unstyled mb-0">
            <li class="mb-1"><a routerLink="/" class="mobile-nav-link" (click)="isMenuOpen=false"><i class="fa fa-home me-2 text-gold"></i>Home</a></li>
            <li class="mb-1"><a routerLink="/products" class="mobile-nav-link" (click)="isMenuOpen=false"><i class="fa fa-store me-2 text-gold"></i>Shop All</a></li>
            <li class="mb-1"><a routerLink="/cart" class="mobile-nav-link" (click)="isMenuOpen=false"><i class="fa fa-shopping-bag me-2 text-gold"></i>Cart ({{ cartService.totalItems() }})</a></li>
            <li *ngIf="!authService.isAuthenticated()" class="mt-2 pt-2 border-top">
              <a routerLink="/login" class="glass-button w-100 justify-content-center" (click)="isMenuOpen=false">Login</a>
            </li>
            <li *ngIf="authService.isAuthenticated()" class="mb-1">
              <a routerLink="/orders" class="mobile-nav-link" (click)="isMenuOpen=false"><i class="fa fa-box me-2 text-gold"></i>My Orders</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  `,
  styles: [`
    .logo-icon {
      width: 36px; height: 36px;
      background: linear-gradient(135deg, var(--primary-gold), #e0b84a);
      border-radius: 10px;
      color: #fff;
      font-size: 1rem;
      box-shadow: 0 4px 12px rgba(201,162,39,0.35);
    }
    .logo-text {
      font-size: 1.1rem;
      font-weight: 700;
      color: var(--text-primary);
    }
    .nav-action-btn {
      width: 40px; height: 40px;
      display: flex; align-items: center; justify-content: center;
      background: rgba(255,255,255,0.5);
      border: 1px solid var(--glass-border);
      border-radius: 10px;
      color: var(--text-primary);
      font-size: 1rem;
      cursor: pointer;
      transition: all 0.2s;
      backdrop-filter: blur(8px);
    }
    .nav-action-btn:hover {
      background: rgba(201,162,39,0.12);
      border-color: var(--primary-gold);
      color: var(--primary-gold);
    }
    .cart-count {
      position: absolute;
      top: -6px; right: -6px;
      background: var(--primary-gold);
      color: #fff;
      font-size: 0.65rem;
      font-weight: 700;
      width: 18px; height: 18px;
      border-radius: 50%;
      display: flex; align-items: center; justify-content: center;
      border: 2px solid white;
    }
    .mobile-nav-link {
      display: block;
      padding: 0.65rem 0.85rem;
      border-radius: 8px;
      color: var(--text-primary);
      text-decoration: none;
      font-weight: 500;
      font-size: 0.9rem;
      transition: background 0.2s;
    }
    .mobile-nav-link:hover { background: rgba(201,162,39,0.08); color: var(--primary-gold); }
    .text-gold { color: var(--primary-gold); }
    .fs-8 { font-size: 0.7rem; }
  `]
})
export class NavbarComponent {
  isScrolled = false;
  isMenuOpen = false;

  constructor(public authService: AuthService, public cartService: CartService, private router: Router) {}

  @HostListener('window:scroll')
  onScroll() { this.isScrolled = window.scrollY > 40; }

  isAdmin(): boolean {
    const r = this.authService.currentUser()?.role;
    return r === 'ROLE_ADMIN' || r === 'ADMIN';
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
