import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common'; 
import { DecimalPipe } from '@angular/common';
import { InventoryService } from '../../services/inventory.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './inventory.component.html',
  styleUrl: './inventory.component.css'
})
export class InventoryComponent implements OnInit {

  inventory: any[] = [];
  filtered: any[] = [];
  history: any[] = [];
  searchTerm = '';
  filterCategory = '';
  activeTab: 'stock' | 'history' = 'stock';
  selectedProduct: any = null;
  loading = false;

  categories: string[] = [];

  constructor(
    private inventoryService: InventoryService,
    private toastr: ToastrService
  ) {}

  ngOnInit() { this.loadInventory(); }

  loadInventory() {
    this.loading = true;
    this.inventoryService.getInventory().subscribe({
      next: (data) => {
        this.inventory = data;
        this.categories = [...new Set(data.map((p: any) => p.category))];
        this.applyFilter();
        this.loading = false;
      },
      error: () => { this.toastr.error('Failed to load inventory'); this.loading = false; }
    });
  }

  applyFilter() {
    let data = this.inventory;
    if (this.searchTerm) {
      const t = this.searchTerm.toLowerCase();
      data = data.filter(p => p.productName.toLowerCase().includes(t) || p.brand?.toLowerCase().includes(t));
    }
    if (this.filterCategory) {
      data = data.filter(p => p.category === this.filterCategory);
    }
    this.filtered = data;
  }

  viewHistory(product: any) {
    this.selectedProduct = product;
    this.activeTab = 'history';
    this.inventoryService.getProductHistory(product.productId).subscribe({
      next: (data) => this.history = data,
      error: () => this.toastr.error('Failed to load history')
    });
  }

  get totalStockValue(): number {
    return this.inventory.reduce((s, p) => s + (p.stockValue || 0), 0);
  }

  get lowStockCount(): number {
    return this.inventory.filter(p => p.stock <= 5).length;
  }

  get outOfStockCount(): number {
    return this.inventory.filter(p => p.stock === 0).length;
  }
}
