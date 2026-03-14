import { Component } from '@angular/core';
import { ConfirmService } from '../../services/confirm.service';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  template: `
    @if (confirmService.visible) {
      <div class="cd-backdrop" (click)="confirmService.resolve(false)">
        <div class="cd-box" (click)="$event.stopPropagation()">

          <div class="cd-icon"
            [class.cd-icon-danger]="confirmService.options.type === 'danger'"
            [class.cd-icon-warning]="confirmService.options.type === 'warning'">
            {{ confirmService.options.type === 'danger' ? '🗑️' : '⚠️' }}
          </div>

          <div class="cd-title">{{ confirmService.options.title }}</div>
          <div class="cd-message">{{ confirmService.options.message }}</div>

          <div class="cd-actions">
            <button class="cd-cancel"
              (click)="confirmService.resolve(false)">
              {{ confirmService.options.cancelText || 'Cancel' }}
            </button>
            <button class="cd-confirm"
              [class.cd-confirm-danger]="confirmService.options.type === 'danger'"
              [class.cd-confirm-warning]="confirmService.options.type === 'warning'"
              (click)="confirmService.resolve(true)">
              {{ confirmService.options.confirmText || 'Confirm' }}
            </button>
          </div>

        </div>
      </div>
    }
  `,
  styles: [`
    .cd-backdrop {
      position: fixed; inset: 0;
      background: rgba(0,0,0,0.5);
      display: flex; align-items: center; justify-content: center;
      z-index: 9999;
      animation: cdFadeIn 0.15s ease;
    }
    .cd-box {
      background: #fff;
      border-radius: 20px;
      padding: 32px 28px;
      width: 380px;
      max-width: calc(100vw - 32px);
      text-align: center;
      box-shadow: 0 24px 64px rgba(0,0,0,0.18);
      animation: cdSlideUp 0.2s ease;
    }
    .cd-icon {
      font-size: 40px;
      margin-bottom: 16px;
      width: 72px; height: 72px;
      border-radius: 50%;
      display: flex; align-items: center; justify-content: center;
      margin: 0 auto 16px;
    }
    .cd-icon-danger  { background: rgba(239,68,68,0.1); }
    .cd-icon-warning { background: rgba(245,158,11,0.1); }
    .cd-title {
      font-family: 'Syne', sans-serif;
      font-size: 18px; font-weight: 700;
      color: #111827; margin-bottom: 10px;
    }
    .cd-message {
      font-size: 14px; color: #6b7280;
      line-height: 1.6; margin-bottom: 28px;
    }
    .cd-actions {
      display: flex; gap: 10px; justify-content: center;
    }
    .cd-cancel {
      flex: 1; padding: 11px 20px;
      border: 1px solid #e5e7eb; border-radius: 10px;
      background: #fff; color: #374151;
      font-size: 14px; font-weight: 500;
      cursor: pointer; transition: all 0.2s;
      font-family: 'DM Sans', sans-serif;
    }
    .cd-cancel:hover { background: #f9fafb; border-color: #d1d5db; }
    .cd-confirm {
      flex: 1; padding: 11px 20px;
      border: none; border-radius: 10px;
      font-size: 14px; font-weight: 600;
      cursor: pointer; transition: all 0.2s;
      font-family: 'DM Sans', sans-serif;
    }
    .cd-confirm-danger  { background: #ef4444; color: #fff; }
    .cd-confirm-danger:hover  { background: #dc2626; }
    .cd-confirm-warning { background: #f59e0b; color: #fff; }
    .cd-confirm-warning:hover { background: #d97706; }
    @keyframes cdFadeIn  { from { opacity: 0; } to { opacity: 1; } }
    @keyframes cdSlideUp {
      from { opacity: 0; transform: translateY(16px) scale(0.97); }
      to   { opacity: 1; transform: translateY(0)    scale(1); }
    }
  `]
})
export class ConfirmDialogComponent {
  constructor(public confirmService: ConfirmService) {}
}