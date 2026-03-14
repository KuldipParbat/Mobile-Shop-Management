import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { ProductService } from '../../../services/product.service';
import { Product } from '../../../models/product.model';
import { ToastrService } from 'ngx-toastr';
import { ConfirmService } from '../../../shared/services/confirm.service';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css'
})
export class ProductsComponent implements OnInit {

  products: Product[] = [];
  filtered: Product[] = [];

  searchTerm     = '';
  filterCategory = '';
  filterTracking = '';
  categories: string[] = [];

  showForm  = false;
  editMode  = false;
  editId: number | null = null;
  loading   = false;
  saving    = false;

  // ── Pagination ──
  page      = 1;
  pageSize  = 10;
  pageSizes = [10, 25, 50];

  product: Product = this.emptyProduct();

  constructor(
    private productService: ProductService,
    private toastr: ToastrService,
    private confirmService: ConfirmService
  ) {}

  ngOnInit() { this.loadProducts(); }

  loadProducts() {
    this.loading = true;
    this.productService.getProducts().subscribe({
      next: (data) => {
        this.products   = data;
        this.categories = [...new Set(data.map(p => p.category))];
        this.applyFilter();
        this.loading    = false;
      },
      error: () => {
        this.toastr.error('Failed to load products');
        this.loading = false;
      }
    });
  }

  // ── Filter ──
  applyFilter() {
    let data = this.products;

    if (this.searchTerm) {
      const t = this.searchTerm.toLowerCase();
      data = data.filter(p =>
        p.name.toLowerCase().includes(t)     ||
        p.brand.toLowerCase().includes(t)    ||
        p.model.toLowerCase().includes(t)    ||
        p.category.toLowerCase().includes(t)
      );
    }

    if (this.filterCategory) {
      data = data.filter(p => p.category === this.filterCategory);
    }

    if (this.filterTracking) {
      data = data.filter(p => p.trackingType === this.filterTracking);
    }

    this.filtered = data;
    this.page = 1; // reset to page 1 on filter change
  }

  clearFilters() {
    this.searchTerm     = '';
    this.filterCategory = '';
    this.filterTracking = '';
    this.applyFilter();
  }

  // ── Pagination ──
  get totalPages(): number {
    return Math.ceil(this.filtered.length / this.pageSize) || 1;
  }

  get paginatedProducts(): Product[] {
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
    this.product  = this.emptyProduct();
    this.showForm = true;
  }

  openEdit(p: Product) {
    this.editMode = true;
    this.editId   = p.id!;
    this.product  = { ...p };
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.saving   = false;
  }

  // ── Validation ──
  validate(): boolean {
    if (!this.product.name?.trim()) {
      this.toastr.warning('Product name is required'); return false;
    }
    if (!this.product.brand?.trim()) {
      this.toastr.warning('Brand is required'); return false;
    }
    if (!this.product.category?.trim()) {
      this.toastr.warning('Category is required'); return false;
    }
    if (!this.product.purchasePrice || this.product.purchasePrice <= 0) {
      this.toastr.warning('Purchase price must be greater than 0'); return false;
    }
    if (!this.product.sellingPrice || this.product.sellingPrice <= 0) {
      this.toastr.warning('Selling price must be greater than 0'); return false;
    }
    if (this.product.sellingPrice < this.product.purchasePrice) {
      this.toastr.warning('Selling price cannot be less than purchase price'); return false;
    }
    if (!this.product.trackingType) {
      this.toastr.warning('Tracking type is required'); return false;
    }
    return true;
  }

  save() {
    if (!this.validate()) return;
    this.saving = true;

    if (this.editMode && this.editId) {
      this.productService.updateProduct(this.editId, this.product).subscribe({
        next: () => {
          this.toastr.success('Product updated successfully');
          this.loadProducts();
          this.closeForm();
        },
        error: (err) => {
          this.handleError(err, 'Update failed');
          this.saving = false;
        }
      });
    } else {
      this.productService.addProduct(this.product).subscribe({
        next: () => {
          this.toastr.success('Product added successfully');
          this.loadProducts();
          this.closeForm();
        },
        error: (err) => {
          this.handleError(err, 'Failed to save product');
          this.saving = false;
        }
      });
    }
  }

  
  async delete(id?: number) {
  if (!id) return;
  const confirmed = await this.confirmService.confirm({
    title:       'Delete Product',
    message:     'Are you sure you want to delete this product? This action cannot be undone.',
    confirmText: 'Yes, Delete',
    cancelText:  'Cancel',
    type:        'danger'
  });
  if (!confirmed) return;
  this.productService.deleteProduct(id).subscribe({
    next: () => { this.toastr.success('Product deleted'); this.loadProducts(); },
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

  get margin(): number {
    if (!this.product.purchasePrice || !this.product.sellingPrice) return 0;
    return Math.round(
      ((this.product.sellingPrice - this.product.purchasePrice)
        / this.product.sellingPrice) * 100
    );
  }

  emptyProduct(): Product {
    return {
      name: '', brand: '', model: '', category: '',
      purchasePrice: 0, sellingPrice: 0,
      stock: 0, trackingType: 'QUANTITY'
    };
  }
}