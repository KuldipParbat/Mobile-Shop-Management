import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { TitleCasePipe } from '@angular/common';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [FormsModule, RouterModule,TitleCasePipe],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css'
})
export class ResetPasswordComponent implements OnInit {

  token           = '';
  newPassword     = '';
  confirmPassword = '';
  loading         = false;
  validating      = true;
  tokenValid      = false;
  success         = false;
  showPass        = false;
  showConfirmPass = false;

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.token = this.route.snapshot.queryParams['token'] || '';

    if (!this.token) {
      this.validating = false;
      this.tokenValid = false;
      return;
    }

    this.authService.validateResetToken(this.token).subscribe({
      next: () => {
        this.tokenValid  = true;
        this.validating  = false;
      },
      error: () => {
        this.tokenValid  = false;
        this.validating  = false;
      }
    });
  }

  get passwordStrength(): 'weak' | 'medium' | 'strong' {
    const p = this.newPassword;
    if (p.length < 6) return 'weak';
    if (p.length >= 8 && /[A-Z]/.test(p) &&
        /[0-9]/.test(p)) return 'strong';
    return 'medium';
  }

  submit() {
    if (!this.newPassword) {
      this.toastr.warning('Enter new password'); return;
    }
    if (this.newPassword.length < 8) {
      this.toastr.warning('Password must be at least 8 characters'); return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.toastr.warning('Passwords do not match'); return;
    }

    this.loading = true;
    this.authService.resetPassword(
      this.token, this.newPassword, this.confirmPassword
    ).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err) => {
        try {
          const body = typeof err.error === 'string'
            ? JSON.parse(err.error) : err.error;
          this.toastr.error(body?.message || 'Reset failed');
        } catch { this.toastr.error('Reset failed'); }
        this.loading = false;
      }
    });
  }
}
