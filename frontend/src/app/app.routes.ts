import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { authGuard, adminGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: '',
        loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
      },
      {
        path: 'products',
        loadComponent: () => import('./features/products/products.component').then(m => m.ProductsComponent)
      },
      {
        path: 'products/:id',
        loadComponent: () => import('./features/products/product-details.component').then(m => m.ProductDetailsComponent)
      },
      {
        path: 'cart',
        loadComponent: () => import('./features/cart/cart.component').then(m => m.CartComponent)
      },
      {
        path: 'checkout',
        canActivate: [authGuard],
        loadComponent: () => import('./features/checkout/checkout.component').then(m => m.CheckoutComponent)
      },
      {
        path: 'order-success/:orderId',
        canActivate: [authGuard],
        loadComponent: () => import('./features/orders/order-success.component').then(m => m.OrderSuccessComponent)
      },
      {
        path: 'orders',
        canActivate: [authGuard],
        loadComponent: () => import('./features/orders/orders.component').then(m => m.OrdersComponent)
      },
      {
        path: 'orders/:id',
        canActivate: [authGuard],
        loadComponent: () => import('./features/orders/order-details.component').then(m => m.OrderDetailsComponent)
      },
      {
        path: 'login',
        loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent)
      },
      {
        path: 'register',
        loadComponent: () => import('./features/auth/register.component').then(m => m.RegisterComponent)
      },
      {
        path: 'profile',
        canActivate: [authGuard],
        loadComponent: () => import('./features/auth/profile.component').then(m => m.ProfileComponent)
      },
      {
        path: 'admin',
        canActivate: [adminGuard],
        children: [
          {
            path: '',
            loadComponent: () => import('./features/admin/admin-dashboard.component').then(m => m.AdminDashboardComponent)
          },
          {
            path: 'inventory',
            loadComponent: () => import('./features/admin/admin-inventory.component').then(m => m.AdminInventoryComponent)
          },
          {
            path: 'orders',
            loadComponent: () => import('./features/admin/admin-orders.component').then(m => m.AdminOrdersComponent)
          }
        ]
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
