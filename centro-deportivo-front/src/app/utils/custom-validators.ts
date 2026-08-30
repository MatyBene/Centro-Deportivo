import { AbstractControl, ValidationErrors } from '@angular/forms';

export class CustomValidators {
  static noWhitespace(control: AbstractControl): ValidationErrors | null {
    const isWhitespace = (control.value || '').trim().length === 0;
    const isValid = !isWhitespace;
    return isValid ? null : { 'whitespace': true };
  }

  static notInFuture(control: AbstractControl): ValidationErrors | null {
    if (!control.value) {
      return null;
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const selectedDate = new Date(control.value);
    if (isNaN(selectedDate.getTime())) {
      return null;
    }

    return selectedDate > today ? { 'notInFuture': true } : null;
  }
}