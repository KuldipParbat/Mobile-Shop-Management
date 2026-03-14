import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DecimalPipe, DatePipe } from '@angular/common';
import { ProductService } from '../../../services/product.service';
import { SalesService } from '../../../services/sales.service';
import { Product } from '../../../models/product.model';
import { Sale } from '../../../models/sale.model';
import { ToastrService } from 'ngx-toastr';
import { ConfirmService } from '../../../shared/services/confirm.service';

interface CartItem {
  product: Product;
  quantity: number;
  sellingPrice: number;
  discount: number;
  imeiNumbers: string[];
  imeiInput: string;
}

@Component({
  selector: 'app-sales',
  standalone: true,
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './sales.component.html',
  styleUrl: './sales.component.css'
})
export class SalesComponent implements OnInit {

  products: Product[] = [];
  history: any[] = [];

  selectedProductId = 0;
  sellingPrice = 0;
  discount = 0;
  paymentMode = 'CASH';
  cart: CartItem[] = [];
  activeTab: 'sale' | 'history' = 'sale';

  // ── Date filter ──
  filterFrom = '';
  filterTo   = '';
  isFiltered = false;

  // ── Pagination ──
  page     = 1;
  pageSize = 10;
  pageSizes = [10, 25, 50];

  constructor(
    private productService: ProductService,
    private salesService: SalesService,
    private toastr: ToastrService,
    private confirmService: ConfirmService 
  ) {}

  ngOnInit() {
    this.productService.getProducts().subscribe(d => this.products = d);
    this.loadHistory();
  }

  loadHistory() {
    this.salesService.getAllSales().subscribe({
      next: (data) => { this.history = data; this.page = 1; },
      error: () => this.toastr.error('Failed to load sales history')
    });
  }

  // ── Date filter ──
  applyDateFilter() {
    if (!this.filterFrom || !this.filterTo) {
      this.toastr.warning('Please select both From and To dates'); return;
    }
    if (this.filterFrom > this.filterTo) {
      this.toastr.warning('From date cannot be after To date'); return;
    }
    this.salesService.getSalesByDateRange(this.filterFrom, this.filterTo).subscribe({
      next: (data) => {
        this.history = data;
        this.isFiltered = true;
        this.page = 1;
        if (data.length === 0) this.toastr.info('No sales found for selected dates');
      },
      error: () => this.toastr.error('Failed to filter sales')
    });
  }

  clearDateFilter() {
    this.filterFrom = '';
    this.filterTo   = '';
    this.isFiltered = false;
    this.loadHistory();
  }

  setQuickFilter(range: 'today' | 'week' | 'month') {
    const today = new Date();
    const fmt = (d: Date) => d.toISOString().split('T')[0];
    this.filterTo = fmt(today);
    if (range === 'today') {
      this.filterFrom = fmt(today);
    } else if (range === 'week') {
      const d = new Date(today);
      d.setDate(d.getDate() - 6);
      this.filterFrom = fmt(d);
    } else {
      const d = new Date(today);
      d.setDate(1);
      this.filterFrom = fmt(d);
    }
    this.applyDateFilter();
  }

  // ── Pagination ──
  get totalPages(): number {
    return Math.ceil(this.history.length / this.pageSize);
  }

  get paginatedHistory(): any[] {
    const start = (this.page - 1) * this.pageSize;
    return this.history.slice(start, start + this.pageSize);
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
    this.page = 1;
  }

  minOf(a: number, b: number): number { return Math.min(a, b); }

  // ── Cart ──
  get selectedProduct(): Product | undefined {
    return this.products.find(p => p.id === +this.selectedProductId);
  }

  onProductSelect() {
    const p = this.selectedProduct;
    if (p) this.sellingPrice = p.sellingPrice;
  }

  addToCart() {
    if (!this.selectedProductId) { this.toastr.warning('Select a product'); return; }
    if (this.sellingPrice <= 0)  { this.toastr.warning('Enter selling price'); return; }
    const product = this.selectedProduct!;
    if (product.stock <= 0)      { this.toastr.warning('Product out of stock'); return; }
    if (this.cart.find(c => c.product.id === product.id)) {
      this.toastr.warning('Already in cart'); return;
    }
    this.cart.push({
      product,
      quantity: 1,
      sellingPrice: this.sellingPrice,
      discount: this.discount,
      imeiNumbers: [],
      imeiInput: ''
    });
    this.selectedProductId = 0;
    this.sellingPrice = 0;
    this.discount = 0;
  }

  async removeFromCart(i: number) {
  const confirmed = await this.confirmService.confirm({
    title:       'Remove Item',
    message:     'Remove this product from cart?',
    confirmText: 'Remove',
    cancelText:  'Keep',
    type:        'warning'
  });
  if (!confirmed) return;
  this.cart.splice(i, 1);
}

  addImei(item: CartItem) {
    const imei = item.imeiInput.trim();
    if (!imei) return;
    if (imei.length < 15) { this.toastr.warning('IMEI must be at least 15 digits'); item.imeiInput = ''; return; }
    if (item.imeiNumbers.includes(imei)) { this.toastr.warning('IMEI already added'); item.imeiInput = ''; return; }
    item.imeiNumbers.push(imei);
    item.quantity = item.imeiNumbers.length;
    item.imeiInput = '';
  }

  removeImei(item: CartItem, idx: number) {
    item.imeiNumbers.splice(idx, 1);
    item.quantity = item.imeiNumbers.length;
  }

  decreaseQty(item: CartItem) { if (item.quantity > 1) item.quantity--; }
  increaseQty(item: CartItem) {
    if (item.quantity < (item.product.stock || 999)) item.quantity++;
  }

  get cartSubtotal(): number {
    return this.cart.reduce((s, c) => s + (c.sellingPrice * c.quantity) - c.discount, 0);
  }

  get totalDiscount(): number {
    return this.cart.reduce((s, c) => s + c.discount, 0);
  }

  validate(): boolean {
    if (this.cart.length === 0) { this.toastr.warning('Cart is empty'); return false; }
    for (const item of this.cart) {
      if (item.product.trackingType === 'IMEI' && item.imeiNumbers.length === 0) {
        this.toastr.warning(`Add IMEI for ${item.product.name}`); return false;
      }
      if (item.product.trackingType === 'QUANTITY' && item.quantity <= 0) {
        this.toastr.warning(`Set quantity for ${item.product.name}`); return false;
      }
    }
    return true;
  }

  completeSale() {
    if (!this.validate()) return;

    const sale: Sale = {
      saleDate: new Date().toISOString().split('T')[0],
      paymentMode: this.paymentMode,
      items: this.cart.map(c => ({
        productId: c.product.id!,
        quantity: c.product.trackingType === 'IMEI' ? c.imeiNumbers.length : c.quantity,
        sellingPrice: c.sellingPrice,
        discount: c.discount,
        imeiNumbers: c.imeiNumbers
      }))
    };

    this.salesService.saveSale(sale).subscribe({
      next: () => {
        this.toastr.success('Sale completed successfully!');
        this.cart = [];
        this.paymentMode = 'CASH';
        this.loadHistory();
        this.activeTab = 'history';
      },
      error: (err) => {
        try {
          const body = typeof err.error === 'string'
            ? JSON.parse(err.error) : err.error;
          this.toastr.error(body?.message || 'Sale failed');
        } catch {
          this.toastr.error('Sale failed');
        }
      }
    });
  }

  // ── Summary ──
  get totalSalesAmount(): number {
    return this.history.reduce((s, h) => s + (h.totalAmount || 0), 0);
  }
}