import { Component, OnInit } from '@angular/core';
import { ITransaction } from '../../../model/transaction.model';
import { Wallet } from '../../../services/wallet';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';


@Component({
  selector: 'app-wallet-transactions',
  imports: [
    CommonModule,
    TableModule,
    ProgressSpinnerModule,
    ToastModule
    ],
  providers: [MessageService],  
  templateUrl: './wallet-transactions.html',
  styleUrl: './wallet-transactions.css'
})
export class WalletTransactionsComponent implements OnInit {

  transactions: ITransaction[] = [];
  isLoading: boolean = false;

  constructor(
    private walletService: Wallet, 
    private messageService: MessageService
  ) { }

  loadTransactions(): void {
    this.isLoading = true;

    this.walletService.getAllAccountTransactions().subscribe({
      next: (res: ITransaction[]) => {
        this.transactions = res;
      },
      error: (err) => {
        setTimeout(() => {
          this.isLoading = false;
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed loading transactions'
          });
        }, 2000);
      },
      complete: () => {
        setTimeout(() => {
          this.isLoading = false;
        }, 2000);
      }
    });
  }

  ngOnInit(): void {
    this.loadTransactions();
  }

}
