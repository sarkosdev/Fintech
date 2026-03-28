import { Component, OnInit } from '@angular/core';
import { ITransaction } from '../../../model/transaction.model';
import { Wallet } from '../../../services/wallet';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';


@Component({
  selector: 'app-wallet-transactions',
  imports: [
    CommonModule,
    TableModule
    ],
  templateUrl: './wallet-transactions.html',
  styleUrl: './wallet-transactions.css'
})
export class WalletTransactionsComponent implements OnInit {

  transactions: ITransaction[] = [];

  constructor(private walletService: Wallet) { }

  loadTransactions(): void {
    this.walletService.getAllAccountTransactions().subscribe({
      next: (res: ITransaction[]) => {
        this.transactions = res;
      },
      error: (err) => {
        console.error("Failed to load transactions", err);
      }
    });
  }

  ngOnInit(): void {
    this.loadTransactions();
  }

}
