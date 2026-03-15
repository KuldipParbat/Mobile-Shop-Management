import { Routes } from '@angular/router';
import { LayoutComponent } from './shared/layout/layout.component';
import { authGuard } from './guards/auth.guard';
import { ownerGuard } from './guards/owner.guard';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  // ── Auth ──
  {
    path: 'login',
    loadComponent: () =>
      import('./modules/auth/login/login.component')
      .then(m => m.LoginComponent)
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./modules/auth/forgot-password/forgot-password.component')
      .then(m => m.ForgotPasswordComponent)
  },
  {
    path: 'reset-password',
    loadComponent: () =>
      import('./modules/auth/reset-password/reset-password.component')
      .then(m => m.ResetPasswordComponent)
  },
  // ── Admin ──
  {
    path: 'admin/login',
    loadComponent: () =>
      import('./modules/auth/admin-login/admin-login.component')
      .then(m => m.AdminLoginComponent)
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./modules/auth/admin-dashboard/admin-dashboard.component')
      .then(m => m.AdminDashboardComponent)
  },
  // ── Shop App ──
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./modules/dashboard/dashboard/dashboard.component')
          .then(m => m.DashboardComponent)
      },
      {
        path: 'products',
        loadComponent: () =>
          import('./modules/products/products/products.component')
          .then(m => m.ProductsComponent)
      },
      {
        path: 'suppliers',
        canActivate: [ownerGuard],
        loadComponent: () =>
          import('./modules/suppliers/suppliers/suppliers.component')
          .then(m => m.SuppliersComponent)
      },
      {
        path: 'purchase',
        loadComponent: () =>
          import('./modules/purchase/purchase/purchase.component')
          .then(m => m.PurchaseComponent)
      },
      {
        path: 'sales',
        loadComponent: () =>
          import('./modules/sales/sales/sales.component')
          .then(m => m.SalesComponent)
      },
      {
        path: 'inventory',
        loadComponent: () =>
          import('./modules/inventory/inventory.component')
          .then(m => m.InventoryComponent)
      },
      {
        path: 'reports',
        canActivate: [ownerGuard],
        loadComponent: () =>
          import('./modules/report/report.component')
          .then(m => m.ReportsComponent)
      }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
