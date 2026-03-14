import { Component, OnInit } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportService } from '../../services/report.service';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [DecimalPipe, DatePipe, FormsModule],
  templateUrl: './report.component.html',
  styleUrl: './report.component.css'
})
export class ReportsComponent implements OnInit {

  activeTab: 'daily' | 'weekly' | 'product' | 'payment' | 'profitloss' | 'supplier' = 'daily';

  dailySales:        any[] = [];
  weeklySummary:     any[] = [];
  productSales:      any[] = [];
  paymentMode:       any[] = [];
  profitLoss:        any[] = [];
  supplierPurchases: any[] = [];

  loading    = false;
  isFiltered = false;

  // ── Date filter ──
  filterFrom = '';
  filterTo   = '';

  private charts: Chart[] = [];

  constructor(
    private reportService: ReportService,
    private toastr: ToastrService
  ) {}

  ngOnInit() { this.loadAll(); }

  // ── Load all with optional date filter ──
  loadAll(from?: string, to?: string) {
    this.loading = true;
    this.destroyCharts();

    forkJoin({
      daily:    this.reportService.getDailySales(from, to),
      weekly:   this.reportService.getWeeklySummary(),
      product:  this.reportService.getProductSales(),
      payment:  this.reportService.getPaymentMode(from, to),
      profit:   this.reportService.getProfitLoss(from, to),
      supplier: this.reportService.getSupplierPurchases(from, to)
    }).subscribe({
      next: (data) => {
        this.dailySales        = data.daily;
        this.weeklySummary     = data.weekly;
        this.productSales      = data.product;
        this.paymentMode       = data.payment;
        this.profitLoss        = data.profit;
        this.supplierPurchases = data.supplier;
        this.loading           = false;
        setTimeout(() => this.initCharts(), 50);
      },
      error: () => {
        this.toastr.error('Failed to load reports');
        this.loading = false;
      }
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
    this.isFiltered = true;
    this.loadAll(this.filterFrom, this.filterTo);
  }

  clearDateFilter() {
    this.filterFrom = '';
    this.filterTo   = '';
    this.isFiltered = false;
    this.loadAll();
  }

  setQuickFilter(range: 'today' | 'week' | 'month' | 'quarter') {
    const today = new Date();
    const fmt   = (d: Date) => d.toISOString().split('T')[0];
    this.filterTo = fmt(today);
    if (range === 'today') {
      this.filterFrom = fmt(today);
    } else if (range === 'week') {
      const d = new Date(today); d.setDate(d.getDate() - 6);
      this.filterFrom = fmt(d);
    } else if (range === 'month') {
      const d = new Date(today); d.setDate(1);
      this.filterFrom = fmt(d);
    } else {
      const d = new Date(today); d.setMonth(d.getMonth() - 3);
      this.filterFrom = fmt(d);
    }
    this.isFiltered = true;
    this.loadAll(this.filterFrom, this.filterTo);
  }

  // ── Tab switch ──
  switchTab(tab: any) {
    this.activeTab = tab;
    this.destroyCharts();
    setTimeout(() => this.initCharts(), 50);
  }

  // ── Charts ──
  destroyCharts() {
    this.charts.forEach(c => c.destroy());
    this.charts = [];
  }

  initCharts() {
    Chart.defaults.color      = '#6b7280';
    Chart.defaults.font.family = "'DM Sans', sans-serif";

    if (this.activeTab === 'daily') {
      const el = document.getElementById('dailyChart') as HTMLCanvasElement;
      if (el && this.dailySales.length > 0) {
        this.charts.push(new Chart(el, {
          type: 'bar',
          data: {
            labels: [...this.dailySales].reverse().map(d => d.date),
            datasets: [{
              label: 'Sales (₹)',
              data: [...this.dailySales].reverse().map(d => d.totalSales),
              backgroundColor: 'rgba(79,142,247,0.8)',
              borderRadius: 6
            }]
          },
          options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: {
              y: { ticks: { callback: v => '₹' + (+v / 1000) + 'k' } }
            }
          }
        }));
      }
    }

    if (this.activeTab === 'profitloss') {
      const el = document.getElementById('profitChart') as HTMLCanvasElement;
      if (el && this.profitLoss.length > 0) {
        this.charts.push(new Chart(el, {
          type: 'line',
          data: {
            labels: [...this.profitLoss].reverse().map(d => d.date),
            datasets: [
              {
                label: 'Revenue',
                data: [...this.profitLoss].reverse().map(d => d.totalRevenue),
                borderColor: '#4f8ef7', tension: 0.4, fill: false, borderWidth: 2
              },
              {
                label: 'Cost',
                data: [...this.profitLoss].reverse().map(d => d.totalCost),
                borderColor: '#ef4444', tension: 0.4, fill: false, borderWidth: 2
              },
              {
                label: 'Profit',
                data: [...this.profitLoss].reverse().map(d => d.profit),
                borderColor: '#10b981', tension: 0.4, fill: false, borderWidth: 2
              }
            ]
          },
          options: {
            responsive: true,
            scales: {
              y: { ticks: { callback: v => '₹' + (+v / 1000) + 'k' } }
            }
          }
        }));
      }
    }

    if (this.activeTab === 'payment') {
      const el = document.getElementById('paymentChart') as HTMLCanvasElement;
      if (el && this.paymentMode.length > 0) {
        this.charts.push(new Chart(el, {
          type: 'doughnut',
          data: {
            labels: this.paymentMode.map(p => p.paymentMode),
            datasets: [{
              data: this.paymentMode.map(p => p.totalAmount),
              backgroundColor: ['#4f8ef7', '#10b981', '#f59e0b'],
              borderWidth: 0
            }]
          },
          options: {
            responsive: true,
            cutout: '65%',
            plugins: {
              legend: { position: 'bottom' }
            }
          }
        }));
      }
    }
  }

  // ── Computed totals ──
  get totalRevenue(): number {
    return this.profitLoss.reduce((s, d) => s + d.totalRevenue, 0);
  }

  get totalProfit(): number {
    return this.profitLoss.reduce((s, d) => s + d.profit, 0);
  }

  get avgMargin(): number {
    if (!this.profitLoss.length) return 0;
    return Math.round(
      this.profitLoss.reduce((s, d) => s + d.margin, 0) / this.profitLoss.length
    );
  }

  get totalDailySales(): number {
    return this.dailySales.reduce((s, d) => s + d.totalSales, 0);
  }
}