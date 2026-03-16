import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IBalanceResponse } from '../model/balance-response.model';
import { IWallet } from '../model/wallet.model';
import { ITransaction } from '../model/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class Wallet {

  private walletApi = 'http://localhost:8080/api/fintech/transaction';
  private accountApi = 'http://localhost:8080/api/fintech/account';

  constructor(private http: HttpClient) { }

  // deposit money into the wallet
  deposit(amount: number) {
    return this.http.post(
      `${this.accountApi}/deposit?amount=${amount}`,
      {}
    );
  }

  // deposit money into the wallet
  getAccountBalance(): Observable<IBalanceResponse> {
    return this.http.get<IBalanceResponse>(
      `${this.accountApi}/balance`,
      {}
    );
  }
  
  // get all wallets available in the system
  getAllWalletsAvailable(): Observable<IWallet[]> {
    return this.http.get<IWallet[]>(`${this.accountApi}/get-all-accounts-system`);
  }

  // withdraw money from the wallet
  withdraw(receiverId: String, amount: number) {
    return this.http.post(
      `${this.walletApi}/withdraw?receiverId=${receiverId}&amount=${amount}`,
      {}
    );
  }

  // get all transactions for the account
  getAllAccountTransactions(): Observable<ITransaction[]> {
    return this.http.get<ITransaction[]>(`${this.walletApi}/wallet-transactions-history`);
  }


}
