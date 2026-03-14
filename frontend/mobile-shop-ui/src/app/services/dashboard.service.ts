import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardDTO {
  todaySales: number;
  todayProfit: number;
  totalProducts: number;
  totalSuppliers: number;
  lowStockProducts: number;
}

export interface WeeklySalesDTO {
  labels: string[];
  sales: number[];
  profits: number[];
}

export interface LowStockProductDTO {
  name: string;
  category: string;
  stock: number;
}

export interface CategoryStatDTO {
  name: string;
  pct: number;
  color: string;
}

@Injectable({ providedIn: 'root' })
export class DashboardService {

  private api = 'http://localhost:8777/api/dashboard';

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<DashboardDTO> {
    return this.http.get<DashboardDTO>(this.api);
  }

  getWeeklySales(): Observable<WeeklySalesDTO> {
    return this.http.get<WeeklySalesDTO>(`${this.api}/weekly-sales`);
  }

  getLowStockProducts(): Observable<LowStockProductDTO[]> {
    return this.http.get<LowStockProductDTO[]>(`${this.api}/low-stock`);
  }

  getCategoryStats(): Observable<CategoryStatDTO[]> {
    return this.http.get<CategoryStatDTO[]>(`${this.api}/category-stats`);
  }
}
