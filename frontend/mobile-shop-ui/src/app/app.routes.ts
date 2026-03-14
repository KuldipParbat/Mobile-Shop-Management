import { Routes } from '@angular/router';
import { LayoutComponent } from './shared/layout/layout.component';

export const routes: Routes = [

  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./modules/dashboard/dashboard/dashboard.component')
          .then(m => m.DashboardComponent)
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'products',
        loadComponent: () =>
          import('./modules/products/products/products.component')
          .then(m => m.ProductsComponent)
      },
      {
        path: 'suppliers',
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
        path: 'report',
        loadComponent: () =>
          import('./modules/report/report.component')
          .then(m => m.ReportsComponent)
      }
    ]
  }

];
