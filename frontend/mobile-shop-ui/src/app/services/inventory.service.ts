import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class InventoryService {
  private api = 'http://localhost:8777/api/inventory';
  constructor(private http: HttpClient) {}

  getInventory(): Observable<any[]> {
    return this.http.get<any[]>(this.api);
  }

  getProductHistory(productId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/history/${productId}`);
  }
}
