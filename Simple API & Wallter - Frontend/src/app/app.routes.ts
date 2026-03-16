import { Routes } from '@angular/router';
import { Home } from './pages/home/home';

// Define application routes
// Each route maps a URL path to a component
export const routes: Routes = [

    //Map the default path to the Home component
    {
        path: 'home', component: Home
    },
    
    // Login Service route
    {
        path: 'login',
        loadComponent: () => 
            import('./pages/login/login').then(m => m.LoginComponent)
    },

    // Register Service route
    {
        path: 'register',
        loadComponent: () => 
            import('./pages/register/register').then(m => m.RegisterComponent),
        data: {
            skipHydration: true
        }
    },

    // Deposit Money Service route
    {
        path: 'services/deposit-money',
        loadComponent: () => 
            import('./pages/wallet/deposit/deposit').then(m => m.Deposit)
    },

    // Transfer Money Service route
    {
        path: 'services/transfer-money',
        loadComponent: () => 
            import('./pages/wallet/transfer/transfer').then(m => m.TransferComponent)
    },

    // Transaction History Service route
    {
        path: 'services/transaction-history',
        loadComponent: () => 
            import('./pages/wallet/wallet-transactions/wallet-transactions').then(m => m.WalletTransactionsComponent)
    }
];
