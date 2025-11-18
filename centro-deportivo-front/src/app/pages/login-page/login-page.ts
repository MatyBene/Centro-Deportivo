import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-page',
  imports: [ReactiveFormsModule],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css'
})
export class LoginPage {
  loginForm: FormGroup;
  isLoading: boolean = false;
  errorMessage: string = '';
  

  constructor(
    private fb: FormBuilder, 
    private authService: AuthService,
    private router: Router
  ){
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    })
  }

  onSubmit(){
    if(this.loginForm.valid && !this.isLoading){
      this.isLoading = true;
      const {username, password} = this.loginForm.value;

      this.authService.login(username, password).subscribe({
        next: (data) => {
          this.isLoading = false;
          this.router.navigate(['/profile']);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error.message;
          console.log('Error: ', error.error.message);
        }
      })
    }
  }
}
