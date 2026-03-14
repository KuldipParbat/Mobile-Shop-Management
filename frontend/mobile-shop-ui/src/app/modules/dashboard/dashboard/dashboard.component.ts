import { Component, OnInit } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import {
  DashboardService, DashboardDTO,
  WeeklySalesDTO, LowStockProductDTO, CategoryStatDTO
} from '../../../services/dashboard.service';
import { ToastrService } from 'ngx-toastr';
import Chart from 'chart.js/auto';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [DecimalPipe, DatePipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  dashboard: DashboardDTO | null = null;
  lowStockProducts: LowStockProductDTO[] = [];
  categories: CategoryStatDTO[] = [];
  today: Date = new Date();
  loading = true;

  private salesChart: Chart | null = null;
  private donutChart: Chart | null = null;

  constructor(
    private dashboardService: DashboardService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    // Load all APIs in parallel
    forkJoin({
      dashboard:     this.dashboardService.getDashboard(),
      weeklySales:   this.dashboardService.getWeeklySales(),
      lowStock:      this.dashboardService.getLowStockProducts(),
      categoryStats: this.dashboardService.getCategoryStats()
    }).subscribe({
      next: ({ dashboard, weeklySales, lowStock, categoryStats }) => {
        this.dashboard        = dashboard;
        this.lowStockProducts = lowStock;
        this.categories       = categoryStats;
        this.loading          = false;
        setTimeout(() => this.initCharts(weeklySales, categoryStats), 0);
      },
      error: () => {
        this.toastr.error('Failed to load dashboard data');
        this.loading = false;
      }
    });
  }

  initCharts(weeklyData: WeeklySalesDTO, categories: CategoryStatDTO[]) {
    if (this.salesChart) this.salesChart.destroy();
    if (this.donutChart) this.donutChart.destroy();

    Chart.defaults.color = '#6b7280';

    this.salesChart = new Chart('salesChart', {
      type: 'line',
      data: {
        labels: weeklyData.labels,
        datasets: [
          {
            label: 'Sales (₹)',
            data: weeklyData.sales,
            borderColor: '#4f8ef7',
            backgroundColor: 'rgba(79,142,247,0.08)',
            fill: true, tension: 0.4, borderWidth: 2.5,
            pointBackgroundColor: '#4f8ef7',
            pointRadius: 4, pointHoverRadius: 6
          },
          {
            label: 'Profit (₹)',
            data: weeklyData.profits,
            borderColor: '#10b981',
            backgroundColor: 'rgba(16,185,129,0.05)',
            fill: true, tension: 0.4, borderWidth: 2,
            pointBackgroundColor: '#10b981',
            pointRadius: 4, pointHoverRadius: 6
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'top', align: 'end' },
          tooltip: {
            backgroundColor: '#fff',
            borderColor: 'rgba(0,0,0,0.08)',
            borderWidth: 1,
            titleColor: '#111827',
            bodyColor: '#6b7280',
            padding: 12,
            callbacks: {
              label: ctx => ' ₹' + (ctx.parsed.y ?? 0).toLocaleString('en-IN')
            }
          }
        },
        scales: {
          x: { grid: { color: 'rgba(0,0,0,0.04)' }, border: { display: false } },
          y: { grid: { color: 'rgba(0,0,0,0.04)' }, border: { display: false },
               ticks: { callback: v => '₹' + (+v / 1000) + 'k' } }
        }
      }
    });

    this.donutChart = new Chart('donutChart', {
      type: 'doughnut',
      data: {
        labels: categories.map(c => c.name),
        datasets: [{
          data: categories.map(c => c.pct),
          backgroundColor: categories.map(c => c.color),
          borderWidth: 0,
          hoverOffset: 6
        }]
      },
      options: {
        responsive: true, cutout: '72%',
        plugins: {
          legend: {
            position: 'bottom',
            labels: { boxWidth: 10, boxHeight: 10, padding: 16, font: { size: 12 } }
          }
        }
      }
    });
  }
}
