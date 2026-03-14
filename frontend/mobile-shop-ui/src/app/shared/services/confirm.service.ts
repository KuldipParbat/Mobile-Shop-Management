import { Injectable } from '@angular/core';

export interface ConfirmOptions {
  title:        string;
  message:      string;
  confirmText?: string;
  cancelText?:  string;
  type?:        'danger' | 'warning';
}

@Injectable({ providedIn: 'root' })
export class ConfirmService {

  visible  = false;
  options: ConfirmOptions = { title: '', message: '' };

  private resolveCallback!: (value: boolean) => void;

  confirm(options: ConfirmOptions): Promise<boolean> {
    this.options = {
      confirmText: 'Delete',
      cancelText:  'Cancel',
      type:        'danger',
      ...options
    };
    this.visible = true;
    return new Promise(resolve => this.resolveCallback = resolve);
  }

  resolve(value: boolean) {
    this.visible = false;
    this.resolveCallback(value);
  }
}