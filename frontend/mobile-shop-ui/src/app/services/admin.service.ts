import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import {
  AdminLoginRequest, AdminLoginResponse,
  TenantStats, CreateTenantForm, TenantUser
} from '../models/admin.model';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AdminService {

  private api      = 'http://localhost:8777/api/admin';
  private ADMIN_KEY = 'mobileshop_admin';

  constructor(private http: HttpClient, private router: Router) {}

  login(dto: AdminLoginRequest): Observable<AdminLoginResponse> {
    return this.http.post<AdminLoginResponse>(
      `${this.api}/login`, dto
    ).pipe(
      tap(res => localStorage.setItem(this.ADMIN_KEY, JSON.stringify(res)))
    );
  }

  logout() {
    localStorage.removeItem(this.ADMIN_KEY);
    this.router.navigate(['/admin/login']);
  }

  getAdmin(): AdminLoginResponse | null {
    const data = localStorage.getItem(this.ADMIN_KEY);
    return data ? JSON.parse(data) : null;
  }

  getToken(): string | null {
    return this.getAdmin()?.token || null;
  }

  isLoggedIn(): boolean {
    const admin = this.getAdmin();
    if (!admin) return false;
    try {
      const payload = JSON.parse(atob(admin.token.split('.')[1]));
      return payload.exp * 1000 > Date.now();
    } catch { return false; }
  }

  // ── Tenant APIs ──
  getAllTenants(): Observable<TenantStats[]> {
    return this.http.get<TenantStats[]>(`${this.api}/tenants`, {
      headers: { Authorization: `Bearer ${this.getToken()}` }
    });
  }

  createTenant(dto: CreateTenantForm): Observable<any> {
    return this.http.post(`${this.api}/tenants`, dto, {
      headers: { Authorization: `Bearer ${this.getToken()}` },
      responseType: 'text'
    });
  }

  toggleTenant(tenantId: string): Observable<any> {
    return this.http.put(
      `${this.api}/tenants/${tenantId}/toggle`, {},
      { headers: { Authorization: `Bearer ${this.getToken()}` },
        responseType: 'text' }
    );
  }

  // ── User APIs ──
  getTenantUsers(tenantId: string): Observable<TenantUser[]> {
    return this.http.get<TenantUser[]>(
      `${this.api}/tenants/${tenantId}/users`,
      { headers: { Authorization: `Bearer ${this.getToken()}` } }
    );
  }

  addStaff(tenantId: string, dto: any): Observable<any> {
    return this.http.post(
      `${this.api}/tenants/${tenantId}/staff`, dto,
      { headers: { Authorization: `Bearer ${this.getToken()}` },
        responseType: 'text' }
    );
  }

  toggleUser(userId: number): Observable<any> {
    return this.http.put(
      `${this.api}/users/${userId}/toggle`, {},
      { headers: { Authorization: `Bearer ${this.getToken()}` },
        responseType: 'text' }
    );
  }

  resetUserPassword(userId: number): Observable<any> {
    return this.http.post(
      `${this.api}/users/${userId}/reset-password`, {},
      { headers: { Authorization: `Bearer ${this.getToken()}` },
        responseType: 'text' }
    );
  }
}