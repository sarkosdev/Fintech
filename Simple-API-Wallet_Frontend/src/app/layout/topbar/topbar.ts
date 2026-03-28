import { Component, OnInit } from '@angular/core';
import { MenubarModule} from "primeng/menubar";
import { MenuItem } from "primeng/api";
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';
import { Router } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-topbar',
  imports: [
    MenubarModule,
    RouterModule,
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './topbar.html',
  styleUrl: './topbar.css'
})


export class Topbar implements OnInit {


  // Menu items for the top bar
  items: MenuItem[] = [];

  userEmail: string | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService 
  ) { }

  

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe(() => {
      //this.userEmail = this.authService.getEmail();
      this.buildMenu(); 
    });
  }

  buildMenu(): void {
    if (this.authService.isLogged()) {
      this.items = [

        {label: 'Home', icon: 'pi pi-home', routerLink: '/home' },

        {label: 'Wallet', icon: 'pi pi-wallet',
          items: [
            { label: 'Deposit Money', routerLink: '/services/deposit-money' },
            { label: 'Transfer Money', routerLink: '/services/transfer-money' },
            { label: 'Check Wallet Transactions', routerLink: '/services/transaction-history' }
          ] 
        },

        {label: 'Logout', icon: 'pi pi-sign-out', 
          command: () => {
            this.authService.logout();
            this.router.navigate(['/home']);
            this.showLogoutMessage(); // triggers success message on logout
          }
        
        }
      ];
    }
    else {
      this.items = [
        {label: 'Home', icon: 'pi pi-home', routerLink: '/home' },
        {label: 'Login', icon: 'pi pi-user', routerLink: '/login' },
        {label: 'Register', icon: 'pi pi-user-plus', routerLink: '/register' }
      ];
    }

  }


  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
    window.location.reload();
  }

  private showLogoutMessage(): void {
    this.messageService.add({ 
      severity: 'success', 
      summary: 'Logged Out', 
      detail: 'You have been successfully logged out.',
      life: 4000 
    });
  }



}
