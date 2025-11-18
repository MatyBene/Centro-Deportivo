import { Component, input, Input } from '@angular/core';
import { AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-field-error',
  imports: [],
  templateUrl: './field-error.html',
  styleUrl: './field-error.css'
})
export class FieldError {
  control = input<AbstractControl | null>();
  serverError = input<string>();
  customErrors = input<{[key: string]: string}>({});

  get errorMessage(): string | null {
    const control = this.control();
    
    if (!control || !control.errors || !control.touched) {
      return this.serverError() || null;
    }

    const errors = control.errors;

    const errorMessages: { [key: string]: string } = {
      'required': 'Este campo es obligatorio',
      'whitespace': 'Este campo no puede contener solo espacios en blanco',
      'email': 'El email debe tener un formato válido',
      'minlength': `Debe tener mínimo ${errors['minlength']?.requiredLength} caracteres`,
      'maxlength': `Debe tener máximo ${errors['maxlength']?.requiredLength} caracteres`,
      'pattern': 'El formato es inválido',
      ...this.customErrors()
    };

    const priority = ['required', 'whitespace', 'pattern', 'minlength', 'maxlength', 'email'];
    const firstError = priority.find(k => errors[k]) || Object.keys(errors)[0];

    return errorMessages[firstError] || 'Error de validación';
  }
}
