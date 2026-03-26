import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { Topbar } from './layout/topbar/topbar';

@Component({
  selector: 'app-root',
  imports: [
    
    RouterOutlet,
    ButtonModule,
    Topbar
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('plataforma-legal-client-module');
}
