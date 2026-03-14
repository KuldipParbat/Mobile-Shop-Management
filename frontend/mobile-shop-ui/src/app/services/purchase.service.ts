import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Purchase } from '../models/purchase.model';

@Injectable({ providedIn: 'root' })
export class PurchaseService {

  private api = 'http://localhost:8777/api/purchases';

  constructor(private http: HttpClient) {}

  savePurchase(purchase: Purchase): Observable<any> {
    return this.http.post(this.api, purchase, { responseType: 'text' });
  }

  getAllPurchases(): Observable<any[]> {
    return this.http.get<any[]>(this.api);
  }

  getPurchasesByDateRange(from: string, to: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}?from=${from}&to=${to}`);
  }
}