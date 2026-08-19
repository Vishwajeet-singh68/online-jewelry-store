import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { FooterComponent } from '../footer/footer.component';
import { ToastComponent } from '../../shared/components/toast/toast.component';
import { CartService } from '../../core/services/cart.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, FooterComponent, ToastComponent],
  template: `
    <div class="d-flex flex-column min-vh-100 position-relative">
      <app-navbar></app-navbar>
      
      <main class="flex-grow-1 pt-5 mt-4">
        <router-outlet></router-outlet>
      </main>

      <app-footer></app-footer>
      <app-toast></app-toast>
    </div>
  `
})
export class MainLayoutComponent implements OnInit {
  constructor(
    private cartService: CartService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.cartService.loadCart().subscribe({
        error: () => {}
      });
    }
  }
}
