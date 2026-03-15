import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [FormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent {

  email   = '';
  loading = false;
  sent    = false;

  constructor(
    private authService: AuthService,
    private toastr: ToastrService
  ) {}

  submit() {
    if (!this.email) { this.toastr.warning('Enter your email'); return; }

    this.loading = true;
    this.authService.forgotPassword(this.email).subscribe({
      next: () => {
        this.sent    = true;
        this.loading = false;
      },
      error: (err) => {
        try {
          const body = typeof err.error === 'string'
            ? JSON.parse(err.error) : err.error;
          this.toastr.error(body?.message || 'Failed to send reset email');
        } catch { this.toastr.error('Failed to send reset email'); }
        this.loading = false;
      }
    });
  }
}