import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="position:fixed;bottom:1.5rem;right:1.5rem;z-index:9999;display:flex;flex-direction:column;gap:0.5rem;align-items:flex-end;">
      <div *ngFor="let t of toastService.toasts()"
           class="toast-luxury fade-in"
           [style.border-left]="'4px solid ' + borderColor(t.type)">
        <div [style.color]="borderColor(t.type)" style="font-size:1.1rem;flex-shrink:0;">
          <i [class]="iconClass(t.type)"></i>
        </div>
        <div style="flex:1;">
          <div style="font-weight:600;font-size:0.82rem;text-transform:uppercase;letter-spacing:0.05em;opacity:0.6;margin-bottom:2px;">
            {{ t.type }}
          </div>
          <div style="font-size:0.88rem;font-weight:500;color:#1c1c1c;">{{ t.message }}</div>
        </div>
        <button (click)="toastService.remove(t.id)"
                style="background:none;border:none;cursor:pointer;color:#999;font-size:1rem;line-height:1;padding:0;flex-shrink:0;">
          &times;
        </button>
      </div>
    </div>
  `
})
export class ToastComponent {
  constructor(public toastService: ToastService) {}

  borderColor(type: string): string {
    return { success:'#16a34a', error:'#dc2626', warning:'#b45309', info:'#0369a1' }[type] || '#c9a227';
  }

  iconClass(type: string): string {
    return {
      success: 'fa fa-check-circle',
      error:   'fa fa-times-circle',
      warning: 'fa fa-exclamation-triangle',
      info:    'fa fa-info-circle'
    }[type] || 'fa fa-bell';
  }
}
