import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="isOpen" class="modal-backdrop fade show" style="background: rgba(0,0,0,0.5); backdrop-filter: blur(4px);"></div>
    <div *ngIf="isOpen" class="modal d-block tab-index='-1'" role="dialog">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content glass-card p-4">
          <div class="modal-header border-0 pb-0">
            <h5 class="modal-title fw-bold text-dark">{{ title }}</h5>
            <button type="button" class="btn-close" (click)="cancel()"></button>
          </div>
          <div class="modal-body py-3 text-secondary">
            {{ message }}
          </div>
          <div class="modal-footer border-0 pt-0 gap-2">
            <button type="button" class="glass-button-outline btn-sm" (click)="cancel()">{{ cancelText }}</button>
            <button type="button" class="glass-button btn-sm" (click)="confirm()">{{ confirmText }}</button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ConfirmDialogComponent {
  @Input() isOpen = false;
  @Input() title = 'Confirm Action';
  @Input() message = 'Are you sure you want to proceed?';
  @Input() confirmText = 'Confirm';
  @Input() cancelText = 'Cancel';

  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  confirm(): void {
    this.confirmed.emit();
  }

  cancel(): void {
    this.cancelled.emit();
  }
}
