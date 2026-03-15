import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule,RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  email    = '';
  password = '';
  loading  = false;
  showPass = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {
    // redirect if already logged in
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  login() {
    if (!this.email)    { this.toastr.warning('Email is required'); return; }
    if (!this.password) { this.toastr.warning('Password is required'); return; }

    this.loading = true;

    this.authService.login({ email: this.email, password: this.password })
      .subscribe({
        next: () => {
          this.toastr.success('Welcome back!');
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          try {
            const body = typeof err.error === 'string'
              ? JSON.parse(err.error) : err.error;
            this.toastr.error(body?.message || 'Login failed');
          } catch {
            this.toastr.error('Login failed');
          }
          this.loading = false;
        }
      });
  }
}
