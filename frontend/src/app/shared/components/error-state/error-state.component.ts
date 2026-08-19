import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-state',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="glass-card p-5 text-center my-4 border-danger border-opacity-25">
      <div class="mb-3 text-danger fs-1">
        <i class="fa fa-exclamation-triangle"></i>
      </div>
      <h4 class="fw-bold text-dark mb-2">{{ title }}</h4>
      <p class="text-secondary max-w-md mx-auto mb-4">{{ message }}</p>
      <button class="glass-button" (click)="retry.emit()">
        <i class="fa fa-sync me-2"></i> Try Again
      </button>
    </div>
  `,
  styles: [`
    .max-w-md { max-width: 28rem; }
  `]
})
export class ErrorStateComponent {
  @Input() title = 'Connection Issue';
  @Input() message = 'Unable to fetch data from the luxury gateway. Please verify server connection.';
  @Output() retry = new EventEmitter<void>();
}
