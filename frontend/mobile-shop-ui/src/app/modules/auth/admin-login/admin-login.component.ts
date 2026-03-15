import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminService } from '../../../services/admin.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './admin-login.component.html',
  styleUrl: './admin-login.component.css'
})
export class AdminLoginComponent {

  email    = '';
  password = '';
  loading  = false;
  showPass = false;

  constructor(
    private adminService: AdminService,
    private router: Router,
    private toastr: ToastrService
  ) {
    if (this.adminService.isLoggedIn()) {
      this.router.navigate(['/admin']);
    }
  }

  login() {
    if (!this.email)    { this.toastr.warning('Email required'); return; }
    if (!this.password) { this.toastr.warning('Password required'); return; }

    this.loading = true;
    this.adminService.login({ email: this.email, password: this.password })
      .subscribe({
        next: () => {
          this.toastr.success('Welcome, Admin!');
          this.router.navigate(['/admin']);
        },
        error: (err) => {
          try {
            const body = typeof err.error === 'string'
              ? JSON.parse(err.error) : err.error;
            this.toastr.error(body?.message || 'Login failed');
          } catch { this.toastr.error('Login failed'); }
          this.loading = false;
        }
      });
  }
}