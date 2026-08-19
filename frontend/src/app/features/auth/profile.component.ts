import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container py-4">
      <div class="glass-card p-4 p-md-5 max-w-lg mx-auto">
        <div class="text-center mb-4">
          <div class="d-inline-flex p-3 rounded-circle bg-gold bg-opacity-10 text-gold mb-3 fs-1">
            <i class="fa fa-user-circle"></i>
          </div>
          <h3 class="fw-bold font-serif text-dark mb-1">{{ authService.currentUser()?.name || 'User Profile' }}</h3>
          <span class="glass-badge">{{ authService.currentUser()?.role || 'CUSTOMER' }}</span>
        </div>

        <div class="glass-panel text-secondary">
          <div class="d-flex justify-content-between py-2 border-bottom">
            <span class="fw-semibold">User ID</span>
            <span>#{{ authService.currentUser()?.id }}</span>
          </div>
          <div class="d-flex justify-content-between py-2 border-bottom">
            <span class="fw-semibold">Email</span>
            <span>{{ authService.currentUser()?.email }}</span>
          </div>
          <div class="d-flex justify-content-between py-2">
            <span class="fw-semibold">Account Status</span>
            <span class="text-success fw-bold">Active VIP Member</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .text-gold { color: var(--primary-gold); }
    .max-w-lg { max-width: 30rem; }
  `]
})
export class ProfileComponent {
  constructor(public authService: AuthService) {}
}
