import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="container py-5">
      <div class="glass-card p-4 p-md-5 max-w-md mx-auto">
        <div class="text-center mb-4">
          <i class="fa fa-gem text-gold display-4 mb-2"></i>
          <h3 class="fw-bold font-serif text-dark">Create Account</h3>
          <p class="text-muted fs-7">Join LUXE JEWELS for personalized luxury shopping</p>
        </div>

        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
          <div class="mb-3">
            <label class="form-label fw-semibold text-dark fs-7">Full Name</label>
            <input type="text" class="glass-input" formControlName="name" placeholder="Eleanor Vance">
            <div *ngIf="isFieldInvalid('name')" class="text-danger fs-7 mt-1">Full name is required.</div>
          </div>

          <div class="mb-3">
            <label class="form-label fw-semibold text-dark fs-7">Email Address</label>
            <input type="email" class="glass-input" formControlName="email" placeholder="name@example.com">
            <div *ngIf="isFieldInvalid('email')" class="text-danger fs-7 mt-1">Please enter a valid email.</div>
          </div>

          <div class="mb-4">
            <label class="form-label fw-semibold text-dark fs-7">Password</label>
            <input type="password" class="glass-input" formControlName="password" placeholder="••••••••">
            <div *ngIf="isFieldInvalid('password')" class="text-danger fs-7 mt-1">Password must be at least 6 characters.</div>
          </div>

          <button type="submit" class="glass-button w-100 py-3 mb-3" [disabled]="isLoading">
            <i *ngIf="isLoading" class="fa fa-spinner fa-spin me-2"></i>
            Register Account
          </button>

          <div class="text-center">
            <span class="text-muted fs-7">Already have an account? </span>
            <a routerLink="/login" class="text-gold fw-bold text-decoration-none fs-7">Sign In</a>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .text-gold { color: var(--primary-gold); }
    .fs-7 { font-size: 0.85rem; }
    .max-w-md { max-width: 26rem; }
  `]
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.registerForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched || this.isLoading));
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.authService.register(this.registerForm.value).subscribe({
      next: () => {
        this.isLoading = false;
        this.toastService.success('Registration successful! Welcome to LUXE JEWELS');
        this.router.navigate(['/']);
      },
      error: err => {
        this.isLoading = false;
        this.toastService.error(err.error?.message || 'Registration failed');
      }
    });
  }
}
