import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../services/admin.service';
import { DecimalPipe } from '@angular/common';
import {
  TenantStats, CreateTenantForm, TenantUser
} from '../../../models/admin.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {

  tenants:      TenantStats[] = [];
  filtered:     TenantStats[] = [];
  selectedTenant: TenantStats | null = null;
  tenantUsers:  TenantUser[] = [];

  searchTerm    = '';
  activeTab:    'tenants' | 'create' = 'tenants';
  showUsers     = false;
  showAddStaff  = false;
  loading       = false;
  saving        = false;

  // ── Create tenant form ──
  form: CreateTenantForm = this.emptyForm();

  // ── Add staff form ──
  staffForm = { name: '', email: '' };

  constructor(
    private adminService: AdminService,
    private toastr: ToastrService
  ) {}

  ngOnInit() { this.loadTenants(); }

  loadTenants() {
    this.loading = true;
    this.adminService.getAllTenants().subscribe({
      next: (data) => {
        this.tenants  = data;
        this.applyFilter();
        this.loading  = false;
      },
      error: () => {
        this.toastr.error('Failed to load tenants');
        this.loading = false;
      }
    });
  }

  applyFilter() {
    const t = this.searchTerm.toLowerCase();
    this.filtered = this.tenants.filter(ten =>
      ten.shopName.toLowerCase().includes(t)  ||
      ten.ownerName.toLowerCase().includes(t) ||
      ten.city?.toLowerCase().includes(t)     ||
      ten.email.toLowerCase().includes(t)
    );
  }

  // ── Stats ──
  get totalTenants():  number { return this.tenants.length; }
  get activeTenants(): number { return this.tenants.filter(t => t.active).length; }
  get totalUsers():    number {
    return this.tenants.reduce((s, t) => s + t.totalUsers, 0);
  }
  get totalSales():    number {
    return this.tenants.reduce((s, t) => s + t.totalSales, 0);
  }

  // ── Toggle tenant ──
  async toggleTenant(tenant: TenantStats) {
    const action = tenant.active ? 'deactivate' : 'activate';
    if (!confirm(`Are you sure you want to ${action} "${tenant.shopName}"?`)) return;

    this.adminService.toggleTenant(tenant.tenantId).subscribe({
      next: () => {
        this.toastr.success(`Shop ${action}d successfully`);
        this.loadTenants();
      },
      error: () => this.toastr.error(`Failed to ${action} shop`)
    });
  }

  // ── View users ──
  viewUsers(tenant: TenantStats) {
    this.selectedTenant = tenant;
    this.showUsers      = true;
    this.adminService.getTenantUsers(tenant.tenantId).subscribe({
      next: (data) => this.tenantUsers = data,
      error: () => this.toastr.error('Failed to load users')
    });
  }

  // ── Toggle user ──
  toggleUser(user: TenantUser) {
    const action = user.active ? 'deactivate' : 'activate';
    if (!confirm(`${action} user "${user.name}"?`)) return;

    this.adminService.toggleUser(user.id).subscribe({
      next: () => {
        this.toastr.success(`User ${action}d`);
        this.viewUsers(this.selectedTenant!);
      },
      error: () => this.toastr.error('Failed')
    });
  }

  // ── Reset user password ──
  resetPassword(user: TenantUser) {
    if (!confirm(`Reset password for "${user.name}"? New password will be emailed.`)) return;

    this.adminService.resetUserPassword(user.id).subscribe({
      next: () => this.toastr.success('Password reset email sent'),
      error: () => this.toastr.error('Failed to reset password')
    });
  }

  // ── Add staff ──
  addStaff() {
    if (!this.staffForm.name || !this.staffForm.email) {
      this.toastr.warning('Name and email required'); return;
    }
    this.adminService.addStaff(
      this.selectedTenant!.tenantId, this.staffForm
    ).subscribe({
      next: () => {
        this.toastr.success('Staff added. Credentials sent by email.');
        this.staffForm   = { name: '', email: '' };
        this.showAddStaff = false;
        this.viewUsers(this.selectedTenant!);
      },
      error: (err) => {
        try {
          const body = typeof err.error === 'string'
            ? JSON.parse(err.error) : err.error;
          this.toastr.error(body?.message || 'Failed');
        } catch { this.toastr.error('Failed to add staff'); }
      }
    });
  }

  // ── Create tenant ──
  validateForm(): boolean {
    if (!this.form.shopName)  { this.toastr.warning('Shop name required'); return false; }
    if (!this.form.ownerName) { this.toastr.warning('Owner name required'); return false; }
    if (!this.form.email)     { this.toastr.warning('Email required'); return false; }
    if (!this.form.phone)     { this.toastr.warning('Phone required'); return false; }
    if (!this.form.password || this.form.password.length < 8) {
      this.toastr.warning('Password must be at least 8 characters'); return false;
    }
    return true;
  }

  createTenant() {
    if (!this.validateForm()) return;
    this.saving = true;

    this.adminService.createTenant(this.form).subscribe({
      next: () => {
        this.toastr.success(
          'Shop created! Welcome email sent to owner.');
        this.form        = this.emptyForm();
        this.saving      = false;
        this.activeTab   = 'tenants';
        this.loadTenants();
      },
      error: (err) => {
        try {
          const body = typeof err.error === 'string'
            ? JSON.parse(err.error) : err.error;
          this.toastr.error(body?.message || 'Failed to create shop');
        } catch { this.toastr.error('Failed to create shop'); }
        this.saving = false;
      }
    });
  }

  logout() { this.adminService.logout(); }

  emptyForm(): CreateTenantForm {
    return {
      shopName: '', ownerName: '', email: '',
      password: '', phone: '', address: '',
      city: '', state: '', pincode: '', gstNumber: ''
    };
  }
}
