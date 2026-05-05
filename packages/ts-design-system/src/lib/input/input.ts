import { Component, Input, Output, EventEmitter, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, ReactiveFormsModule } from '@angular/forms';

/**
 * InputComponent — text input with label and error state.
 *
 * Implements ControlValueAccessor for seamless Angular Forms integration.
 * Used in search bars and checkout forms.
 *
 * @example
 * <ts-input
 *   label="Buscar tractores"
 *   placeholder="ej. TractorPro X200"
 *   [formControl]="searchControl" />
 */
@Component({
  selector: 'ts-input',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <div class="ts-input-wrapper">
      @if (label) {
        <label class="ts-input__label">{{ label }}</label>
      }
      <input
        class="ts-input"
        [class.ts-input--error]="error"
        [type]="type"
        [placeholder]="placeholder"
        [disabled]="isDisabled"
        [value]="value"
        (input)="onInput($event)"
        (blur)="onTouched()" />
      @if (error) {
        <span class="ts-input__error">{{ error }}</span>
      }
    </div>
  `,
  styles: [`
    .ts-input-wrapper { display: flex; flex-direction: column; gap: 4px; }

    .ts-input__label {
      font-size: 13px;
      font-weight: 500;
      color: var(--color-text-primary, #111827);
    }

    .ts-input {
      padding: 10px 14px;
      border: 1.5px solid var(--color-border, #e5e7eb);
      border-radius: var(--radius-md, 8px);
      font-size: 15px;
      font-family: inherit;
      color: var(--color-text-primary, #111827);
      background: var(--color-bg-primary, #fff);
      transition: border-color 0.15s ease;
      outline: none;
      width: 100%;
      box-sizing: border-box;

      &::placeholder { color: var(--color-text-muted, #6b7280); }
      &:focus { border-color: var(--color-primary, #2f855a); }
      &--error { border-color: #dc2626; }
      &:disabled { background: var(--color-bg-secondary, #f9fafb); cursor: not-allowed; }
    }

    .ts-input__error { font-size: 12px; color: #dc2626; }
  `],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => InputComponent),
    multi: true
  }]
})
export class InputComponent implements ControlValueAccessor {
  @Input() label = '';
  @Input() placeholder = '';
  @Input() type = 'text';
  @Input() error = '';
  @Output() valueChange = new EventEmitter<string>();

  value = '';
  isDisabled = false;
  onTouched = () => {};
  private onChange = (_: string) => {};

  onInput(event: Event): void {
    const val = (event.target as HTMLInputElement).value;
    this.value = val;
    this.onChange(val);
    this.valueChange.emit(val);
  }

  writeValue(val: string): void { this.value = val ?? ''; }
  registerOnChange(fn: (_: string) => void): void { this.onChange = fn; }
  registerOnTouched(fn: () => void): void { this.onTouched = fn; }
  setDisabledState(disabled: boolean): void { this.isDisabled = disabled; }
}