import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="container py-5">
      <div class="glass-card p-4 p-md-5 max-w-md mx-auto">
        <div class="text-center mb-4">
          <i class="fa fa-gem text-gold display-4 mb-2"></i>
          <h3 class="fw-bold font-serif text-dark">Welcome Back</h3>
          <p class="text-muted fs-7">Access your exclusive LUXE JEWELS account</p>
        </div>

        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
          <div class="mb-3">
            <label class="form-label fw-semibold text-dark fs-7">Email Address</label>
            <input type="email" class="glass-input" formControlName="email" placeholder="name@example.com">
            <div *ngIf="isFieldInvalid('email')" class="text-danger fs-7 mt-1">Please enter a valid email address.</div>
          </div>

          <div class="mb-4">
            <label class="form-label fw-semibold text-dark fs-7">Password</label>
            <input type="password" class="glass-input" formControlName="password" placeholder="••••••••">
            <div *ngIf="isFieldInvalid('password')" class="text-danger fs-7 mt-1">Password is required.</div>
          </div>

          <button type="submit" class="glass-button w-100 py-3 mb-3" [disabled]="isLoading">
            <i *ngIf="isLoading" class="fa fa-spinner fa-spin me-2"></i>
            Sign In
          </button>

          <div class="text-center">
            <span class="text-muted fs-7">Don't have an account? </span>
            <a routerLink="/register" class="text-gold fw-bold text-decoration-none fs-7">Create One</a>
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
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  isLoading = false;
  returnUrl = '/';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });

    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.loginForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched || this.isLoading));
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.isLoading = false;
        this.toastService.success('Logged in successfully!');
        this.router.navigateByUrl(this.returnUrl);
      },
      error: err => {
        this.isLoading = false;
        this.toastService.error(err.error?.message || 'Invalid email or password');
      }
    });
  }
}
