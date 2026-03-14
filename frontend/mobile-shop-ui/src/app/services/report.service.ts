import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ReportService {

  private api = 'http://localhost:8777/api/reports';

  constructor(private http: HttpClient) {}

  getDailySales(from?: string, to?: string): Observable<any[]> {
    const params = from && to ? `?from=${from}&to=${to}` : '';
    return this.http.get<any[]>(`${this.api}/daily-sales${params}`);
  }

  getProductSales(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/product-sales`);
  }

  getWeeklySummary(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/weekly-summary`);
  }

  getPaymentMode(from?: string, to?: string): Observable<any[]> {
    const params = from && to ? `?from=${from}&to=${to}` : '';
    return this.http.get<any[]>(`${this.api}/payment-mode${params}`);
  }

  getProfitLoss(from?: string, to?: string): Observable<any[]> {
    const params = from && to ? `?from=${from}&to=${to}` : '';
    return this.http.get<any[]>(`${this.api}/profit-loss${params}`);
  }

  getSupplierPurchases(from?: string, to?: string): Observable<any[]> {
    const params = from && to ? `?from=${from}&to=${to}` : '';
    return this.http.get<any[]>(`${this.api}/supplier-purchases${params}`);
  }
}