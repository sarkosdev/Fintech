import { Component, OnInit } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ILoginRequest, ILoginResponse } from '../../model/login.model';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth';
import { Router } from '@angular/router';
import {MessageService} from 'primeng/api';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-login',
  imports: [
    InputTextModule,
    FormsModule,
    ButtonModule,
    ReactiveFormsModule,
    CommonModule,
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup;

  constructor(
    private fb: FormBuilder, 
    private authService: AuthService, 
    private router: Router,
    private messageService: MessageService
  
  ) {

    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });

  }

  ngOnInit(): void {
    this.initLoginForm();
  }

  
  private initLoginForm(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  submit(): void {

    if (this.loginForm.invalid) {
      return;
    }

    const payload: ILoginRequest = {
      email: this.loginForm.value.email,
      password: this.loginForm.value.password
    };

    this.authService.login(payload).subscribe({
      next: (response: ILoginResponse) => {

        const token = response.token;
        this.authService.saveToken(token);

        //console.log('Login successful, received token:', payload.email);
        
        localStorage.setItem('token', token);
        localStorage.setItem('userEmail', payload.email);

      },
      error: (error) => {
        const message =
          error?.error?.message ||
          (error.status === 401 ? 'Invalid email or password' : 'Login failed');

        this.messageService.add({
          severity: 'error',
          summary: 'Login Failed',
          detail: message
        });
      },
      complete: () => {
        // redirecionar para Home após login bem-sucedido
        this.router.navigate(['/home']);
      }

    });

    // console.log('Form submitted payload', payload);

  }

  // HELPERS FOR VALIDATION

  isInvalid(field: string): boolean {
    const control = this.loginForm.get(field);
    return !!(control && control.invalid && control.touched);
  }

  // Remove spaces from email input
  removeSpacesFromEmail(event: Event): void {
    const input = event.target as HTMLInputElement;

    // Remove ALL whitespace characters and convert to lowercase
    const sanitized = input.value.replace(/\s+/g, '').toLowerCase();

    // Update input + form control without triggering loops
    input.value = sanitized;
    this.loginForm.get('email')?.setValue(sanitized, { emitEvent: false });
  }


}
