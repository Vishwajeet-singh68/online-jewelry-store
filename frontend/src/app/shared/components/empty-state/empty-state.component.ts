import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="glass-card p-5 text-center my-4">
      <div class="mb-3 text-gold fs-1">
        <i [class]="icon"></i>
      </div>
      <h4 class="fw-bold text-dark mb-2">{{ title }}</h4>
      <p class="text-secondary max-w-md mx-auto mb-4">{{ description }}</p>
      <button *ngIf="buttonText" class="glass-button" (click)="action.emit()">
        {{ buttonText }}
      </button>
    </div>
  `,
  styles: [`
    .text-gold { color: var(--primary-gold); }
    .max-w-md { max-width: 28rem; }
  `]
})
export class EmptyStateComponent {
  @Input() icon = 'fa fa-gem';
  @Input() title = 'Nothing to see here';
  @Input() description = 'Explore our timeless luxury collections to find your perfect match.';
  @Input() buttonText = 'Explore Collection';
  @Output() action = new EventEmitter<void>();
}
