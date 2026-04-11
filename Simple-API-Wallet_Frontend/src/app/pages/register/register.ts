import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputText, InputTextModule } from 'primeng/inputtext';
import { AbstractControl, ValidationErrors } from '@angular/forms';
import { AuthService } from '../../services/auth';
import { IRegisterRequest } from '../../model/register.model';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { Router } from '@angular/router';
import { ProgressSpinner } from 'primeng/progressspinner';
import { IApiResponse } from '../../model/api-response.model';

@Component({
  selector: 'app-register',
  imports: [
    InputTextModule,
    FormsModule,
    ButtonModule,
    ReactiveFormsModule,
    CommonModule,
    ToastModule,
    ProgressSpinner
  ],
  providers: [MessageService ],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent implements OnInit {

  isLoading: boolean = false;
  registerForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private messageService: MessageService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(8)]]
    }, {
      validators: [this.passwordMatchValidator.bind(this)]
    });
  }

  submit(): void {
    if (this.registerForm.invalid) {
      return;
    }

    this.isLoading = true;

    const payload: IRegisterRequest = {
      name: this.registerForm.value.username,
      email: this.registerForm.value.email,
      password: this.registerForm.value.password
    };

    let apiResponse: IApiResponse;

    this.authService.register(payload).subscribe({
      next: (response: IApiResponse) => {
        apiResponse = response;
      },
      error: (error) => {
        setTimeout(() => { 
          this.isLoading = false;
          const message = error?.error?.message || error?.error?.error || "Registration failed";

          this.messageService.add({
            severity: 'error',
            summary: 'Registration Error',
            detail: message
          });
        }, 2000);

      }, complete: () => {
        setTimeout(() => {
          this.isLoading = false;
          this.messageService.add({ 
            severity: 'success',
            summary: 'Success',
            detail: apiResponse.message,
            life: 4000 
          });
        }, 2000);
      }
    });
  }


  // HELPER METHODS
  removeSpacesFromEmail(event: Event) {
    const input = event.target as HTMLInputElement;

    input.value = input.value.replace(/\s/g, '');

    this.registerForm.patchValue({
      email: input.value
    });
  }

  isInvalid(field: string): boolean {
    const control = this.registerForm.get(field);
    return !!(control && control.invalid && control.touched);
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {

    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (!password || !confirmPassword) {
      return null;
    }

    if (confirmPassword.errors && !confirmPassword.errors['passwordMismatch']) {
      return null;
    }

    if (password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
    } else {
      confirmPassword.setErrors(null);
    }

    return null;
  }

}
