import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { AppFooter } from '../../components/app-footer/app-footer';
import {
  IonContent,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardContent,
  IonItem,
  IonLabel,
  IonInput,
  IonButton,
  IonText,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonSpinner,
} from '@ionic/angular/standalone';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.html',
  styleUrls: ['./login-page.scss'],
  imports: [
    ReactiveFormsModule,
    IonContent,
    IonCard,
    IonCardHeader,
    IonCardTitle,
    IonCardContent,
    IonItem,
    IonLabel,
    IonInput,
    IonButton,
    IonText,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonSpinner,
    AppFooter,
  ],
})
export class LoginPage {
  loginForm: FormGroup;
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.loginForm.valid && !this.isLoading) {
      this.isLoading = true;
      this.errorMessage = '';
      const { username, password } = this.loginForm.value;

      this.authService.login(username, password).subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['/profile']);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message ?? 'No se pudo conectar con el servidor';
        },
      });
    }
  }
}
