import { Component, OnInit } from '@angular/core';
import { IBalanceResponse } from '../../../model/balance-response.model';
import { Wallet } from '../../../services/wallet';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastModule } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';
import { MessageService } from 'primeng/api';
import { IWallet } from '../../../model/wallet.model';
import {InputNumberModule } from 'primeng/inputnumber';
import { AbstractControl, ValidatorFn } from '@angular/forms';
import { SelectModule } from 'primeng/select';
import { IApiResponse } from '../../../model/api-response.model';
import { ProgressSpinnerModule } from 'primeng/progressspinner';


@Component({
  selector: 'app-transfer',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ToastModule,
    ButtonModule,
    InputNumberModule,
    SelectModule,
    ProgressSpinnerModule
  ],
  providers: [MessageService],
  templateUrl: './transfer.html',
  styleUrl: './transfer.css'
})
export class TransferComponent implements OnInit {

  transferForm!: FormGroup;
  accountBalance: number | undefined;
  amount: number = 0;
  messages: any[] = [];
  walletsAvailable: IWallet[] = [];
  isLoading: boolean = false;


  constructor(
    private transfer: FormBuilder,
    private walletService: Wallet,
    private messageService: MessageService
  ) 
  { }

  ngOnInit(): void {
      this.loadAccountBalance();
      this.initTransferForm();
      this.loadWalletsAvailable();
  }


  submit(): void {
    
    this.isLoading = true;
    let apiResponse: IApiResponse;

    // TRANSFER OPERATION BETWEEN WALLETS
    this.walletService.withdraw(this.transferForm.value.walletId, this.transferForm.value.amount).subscribe({
      next: (response: IApiResponse) => {
        apiResponse = response;
        this.transferForm.reset();
      },
      error: (err: IApiResponse) => {
        setTimeout(() => {
          this.isLoading = false;
          this.messageService.add({ 
            severity: 'error',
            summary: 'Error',
            detail: err.message 
          });
        }, 2000);
      },
      complete: () => {
        setTimeout(() => {
          this.isLoading = false;
          this.messageService.add({ 
            severity: 'success',
            summary: 'Success',
            detail: apiResponse.message
          });
          this.loadAccountBalance();
        }, 2000);
        
        
      }
    });
  
  }


  initTransferForm(): void {
    this.transferForm = this.transfer.group({
      walletId: [null, [Validators.required]],
      amount: [
        0,
        [
          Validators.required,
          Validators.min(0.01),
          this.balanceValidator(() => this.accountBalance)
        ]
      ]
    });
  }


  loadAccountBalance(): void {
      this.walletService.getAccountBalance().subscribe({
        next: (response: IBalanceResponse) => {
          console.log("Account balance:", response.balance);
          this.accountBalance = response.balance as number;
          this.transferForm.get('amount')?.updateValueAndValidity();
        },
        error: (err) => {
          console.error("Failed to load account balance", err);
        }
      });
  }

  loadWalletsAvailable(): void {
    this.walletService.getAllWalletsAvailable().subscribe({
      next: (response: IWallet[]) => {
        this.walletsAvailable = response as IWallet[];
        console.log("Wallets available:", this.walletsAvailable);
      },
      error: (err) => {
        console.error("Failed to load wallets available", err);
      }
    });
  }

  amountIsInvalid(field: string): boolean {
    const control = this.transferForm.get('amount');
    return !!(control && control.invalid && control.touched);
  }


  balanceValidator(getBalance: () => number | undefined): ValidatorFn {
  return (control: AbstractControl) => {

    const balance = getBalance();
    const value = control.value;

    if (balance === undefined || value === null) {
      return null;
    }

    if (value > balance) {
      return { insufficientBalance: true };
    }

    return null;
  };
}

  isInvalid(field: string): boolean {
    const control = this.transferForm.get(field);
    return !!(control && control.invalid && control.touched);
  }

}

