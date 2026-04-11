import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import {InputNumberModule } from 'primeng/inputnumber';
import { ButtonModule } from 'primeng/button';
import { Wallet } from '../../../services/wallet';
import { IBalanceResponse } from '../../../model/balance-response.model';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { IApiResponse } from '../../../model/api-response.model';
import { ProgressSpinnerModule } from 'primeng/progressspinner';



@Component({
  selector: 'app-deposit',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputNumberModule,
    ButtonModule,
    ToastModule,
    ProgressSpinnerModule
  ],
  providers: [MessageService],
  templateUrl: './deposit.html',
  styleUrl: './deposit.css'
})
export class Deposit implements OnInit{

  depositForm!: FormGroup;
  accountBalance: number | undefined;
  amount: number = 0;
  messages: any[] = [];
  isLoading: boolean = false;

  constructor(
    private deposit: FormBuilder, 
    private walletService: Wallet, 
    private messageService: MessageService
  
  ) { }


  ngOnInit(): void {
    this.loadAccountBalance();
    this.initDepositForm();
  }

  initDepositForm(): void {
    this.depositForm = this.deposit.group({
      amount: [0, [Validators.required, Validators.min(1)]]
    });
  }

  loadAccountBalance(): void {
    this.walletService.getAccountBalance().subscribe({
      next: (response: IBalanceResponse) => {
        console.log("Account balance:", response.balance);
        this.accountBalance = response.balance as number;
      },
      error: (err) => {
        console.error("Failed to load account balance", err);
      }
    });
  }

  isInvalid(field: string): boolean {
    const control = this.depositForm.get(field);
    return !!(control && control.invalid && control.touched);
  }

  submit(): void { 
    
    if (this.depositForm.invalid) {
      return;
    }

    this.isLoading = true;
    let apiResponse: IApiResponse;

    const amount = this.depositForm.value.amount;

    this.walletService.deposit(amount).subscribe({
      next: (res: IApiResponse) => {
        apiResponse = res;
        
        this.depositForm.reset({ amount: 0 });
      },
      error: err => {
        setTimeout(() => {
          this.isLoading = false;
          this.messageService.add({
            severity: 'error',
            summary: 'Deposit Failed',
            detail: 'An error occurred during deposit',
            life: 4000
          });
        }, 2000);
      },
      complete: () => { 
        setTimeout(() => {
          this.isLoading = false;
          this.messageService.add({
            severity: 'success',
            summary: 'Deposit Successful',
            detail: apiResponse.message,
            life: 4000
          });
          this.loadAccountBalance();
        }, 2000);
      }
    });



    

  }

}
