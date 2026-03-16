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




@Component({
  selector: 'app-deposit',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputNumberModule,
    ButtonModule,
    ToastModule,
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

  constructor(
    private deposit: FormBuilder, 
    private walletService: Wallet, 
    private messageService: MessageService
  
  ) 
    { }



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
    console.log('Deposit amount:', this.depositForm.value.amount);

    if (this.depositForm.invalid) {
    return;
  }

    const amount = this.depositForm.value.amount;

    this.walletService.deposit(amount).subscribe({
      next: res => {
        this.messageService.add({
          severity: 'success',
          summary: 'Deposit Successful',
          detail: 'New deposit completed with success',
          life: 4000
        });

        this.loadAccountBalance();
        this.depositForm.reset({ amount: 0 });

        console.log("Deposit success", res);
      },
      error: err => {
        this.messageService.add({
          severity: 'error',
          summary: 'Deposit Failed',
          detail: "Something went wrong. Deposit didn't go through.",
          life: 4000
        });
        console.error("Deposit failed", err);
      }
    });



    

  }

}
