import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SupplierService } from '../../../services/supplier.service';
import { Supplier } from '../../../models/supplier.model';
import { ToastrService } from 'ngx-toastr';
import { ConfirmService } from '../../../shared/services/confirm.service';

@Component({
  selector: 'app-suppliers',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './suppliers.component.html',
  styleUrl: './suppliers.component.css'
})
export class SuppliersComponent implements OnInit {

  suppliers: Supplier[] = [];
  filtered:  Supplier[] = [];

  searchTerm = '';
  showForm   = false;
  editMode   = false;
  editId: number | null = null;
  loading    = false;
  saving     = false;

  // ── Pagination ──
  page      = 1;
  pageSize  = 10;
  pageSizes = [10, 25, 50];

  supplier: Supplier = this.empty();

  constructor(
    private supplierService: SupplierService,
    private toastr: ToastrService,
    private confirmService: ConfirmService
  ) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.supplierService.getSuppliers().subscribe({
      next: (data) => {
        this.suppliers = data;
        this.applyFilter();
        this.loading   = false;
      },
      error: () => {
        this.toastr.error('Failed to load suppliers');
        this.loading = false;
      }
    });
  }

  // ── Filter ──
  applyFilter() {
    const t = this.searchTerm.toLowerCase();
    this.filtered = this.suppliers.filter(s =>
      s.name.toLowerCase().includes(t)    ||
      s.phone.includes(t)                 ||
      (s.gstNumber || '').toLowerCase().includes(t) ||
      (s.address   || '').toLowerCase().includes(t)
    );
    this.page = 1;
  }

  clearSearch() {
    this.searchTerm = '';
    this.applyFilter();
  }

  // ── Pagination ──
  get totalPages(): number {
    return Math.ceil(this.filtered.length / this.pageSize) || 1;
  }

  get paginatedSuppliers(): Supplier[] {
    const start = (this.page - 1) * this.pageSize;
    return this.filtered.slice(start, start + this.pageSize);
  }

  get pageNumbers(): number[] {
    const pages: number[] = [];
    const start = Math.max(1, this.page - 2);
    const end   = Math.min(this.totalPages, this.page + 2);
    for (let i = start; i <= end; i++) pages.push(i);
    return pages;
  }

  changePage(p: number) {
    if (p < 1 || p > this.totalPages) return;
    this.page = p;
  }

  changePageSize(size: number) {
    this.pageSize = +size;
    this.page     = 1;
  }

  minOf(a: number, b: number): number { return Math.min(a, b); }

  // ── Form ──
  openAdd() {
    this.editMode = false;
    this.supplier = this.empty();
    this.showForm = true;
  }

  openEdit(s: Supplier) {
    this.editMode = true;
    this.editId   = s.id!;
    this.supplier = { ...s };
    this.showForm = true;
  }

  close() {
    this.showForm = false;
    this.saving   = false;
  }

  // ── Validation ──
  validate(): boolean {
    if (!this.supplier.name?.trim()) {
      this.toastr.warning('Supplier name is required'); return false;
    }
    if (!this.supplier.phone?.trim()) {
      this.toastr.warning('Phone number is required'); return false;
    }
    if (!/^\d{10}$/.test(this.supplier.phone)) {
      this.toastr.warning('Enter a valid 10-digit phone number'); return false;
    }
    if (this.supplier.gstNumber &&
        !/^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$/.test(
          this.supplier.gstNumber)) {
      this.toastr.warning('Enter a valid GST number (e.g. 27ABCDE1234F1Z5)');
      return false;
    }
    return true;
  }

  save() {
    if (!this.validate()) return;
    this.saving = true;

    if (this.editMode && this.editId) {
      this.supplierService.updateSupplier(this.editId, this.supplier).subscribe({
        next: () => {
          this.toastr.success('Supplier updated successfully');
          this.load();
          this.close();
        },
        error: (err) => {
          this.handleError(err, 'Update failed');
          this.saving = false;
        }
      });
    } else {
      this.supplierService.addSupplier(this.supplier).subscribe({
        next: () => {
          this.toastr.success('Supplier added successfully');
          this.load();
          this.close();
        },
        error: (err) => {
          this.handleError(err, 'Failed to save supplier');
          this.saving = false;
        }
      });
    }
  }

  async delete(id?: number) {
  if (!id) return;
  const confirmed = await this.confirmService.confirm({
    title:       'Delete Supplier',
    message:     'Are you sure you want to delete this supplier? This action cannot be undone.',
    confirmText: 'Yes, Delete',
    cancelText:  'Cancel',
    type:        'danger'
  });
  if (!confirmed) return;
  this.supplierService.deleteSupplier(id).subscribe({
    next: () => { this.toastr.success('Supplier deleted'); this.load(); },
    error: (err) => this.handleError(err, 'Delete failed')
  });
}

  // ── Helpers ──
  handleError(err: any, fallback = 'Operation failed') {
    try {
      const body = typeof err.error === 'string'
        ? JSON.parse(err.error) : err.error;
      this.toastr.error(body?.message || fallback);
    } catch {
      this.toastr.error(fallback);
    }
  }

  empty(): Supplier {
    return { name: '', phone: '', address: '', gstNumber: '' };
  }

  get isPhoneInvalid(): boolean {
  return !!(this.supplier.phone &&
    this.supplier.phone.length > 0 &&
    !/^\d{10}$/.test(this.supplier.phone));
}

get isGstIncomplete(): boolean {
  return !!(this.supplier.gstNumber &&
    this.supplier.gstNumber.length > 0 &&
    this.supplier.gstNumber.length < 15);
}
}