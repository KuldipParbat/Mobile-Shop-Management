import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, AuthUser } from '../models/auth.model';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private api     = 'http://localhost:8777/api/auth';
  private USER_KEY = 'mobileshop_user';

  constructor(private http: HttpClient, private router: Router) {}

  login(dto: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.api}/login`, dto).pipe(
      tap(res => localStorage.setItem(this.USER_KEY, JSON.stringify(res)))
    );
  }

  logout() {
    localStorage.removeItem(this.USER_KEY);
    this.router.navigate(['/login']);
  }

  getUser(): AuthUser | null {
    const data = localStorage.getItem(this.USER_KEY);
    return data ? JSON.parse(data) : null;
  }

  getToken(): string | null {
    return this.getUser()?.token || null;
  }

  isLoggedIn(): boolean {
    const user = this.getUser();
    if (!user) return false;
    // check token not expired
    try {
      const payload = JSON.parse(atob(user.token.split('.')[1]));
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  isOwner(): boolean {
    return this.getUser()?.role === 'OWNER';
  }

  isStaff(): boolean {
    return this.getUser()?.role === 'STAFF';
  }

  get shopName(): string {
    return this.getUser()?.shopName || 'Mobile Shop';
  }

  get userName(): string {
    return this.getUser()?.name || '';
  }

  get userRole(): string {
    return this.getUser()?.role || '';
  }

  forgotPassword(email: string): Observable<any> {
  return this.http.post(
    `${this.api}/forgot-password`,
    { email },
    { responseType: 'text' }
  );
}

validateResetToken(token: string): Observable<any> {
  return this.http.get(
    `${this.api}/reset-password/validate?token=${token}`,
    { responseType: 'text' }
  );
}

resetPassword(token: string,
              newPassword: string,
              confirmPassword: string): Observable<any> {
  return this.http.post(
    `${this.api}/reset-password`,
    { token, newPassword, confirmPassword },
    { responseType: 'text' }
  );
}
}
