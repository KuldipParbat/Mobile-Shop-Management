import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Sale } from '../models/sale.model';

@Injectable({ providedIn: 'root' })
export class SalesService {

  private api = 'http://localhost:8777/api/sales';

  constructor(private http: HttpClient) {}

  saveSale(sale: Sale): Observable<any> {
    return this.http.post(this.api, sale, { responseType: 'text' });
  }

  getAllSales(): Observable<any[]> {
    return this.http.get<any[]>(this.api);
  }

  getSalesByDateRange(from: string, to: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}?from=${from}&to=${to}`);
  }
}