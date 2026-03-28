import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { ILoginRequest } from '../model/login.model';
import { isPlatformBrowser } from '@angular/common';
import { IRegisterRequest } from '../model/register.model';
import { IApiResponse } from '../model/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private loggedIn = new BehaviorSubject<boolean>(this.isLogged());
  isLoggedIn$ = this.loggedIn.asObservable();

 //backend api url
  private api = 'http://localhost:8080/api/fintech/auth';

  platformId = inject(PLATFORM_ID);

  constructor(private http: HttpClient) {}

  login(loginRequest: ILoginRequest): Observable<any> {
    return this.http.post(`${this.api}/login`, loginRequest);
  }

  register(registerRequest: IRegisterRequest): Observable<IApiResponse> {
    return this.http.post<IApiResponse>(`${this.api}/register`, registerRequest);
  }

  saveToken(token: string) {
    localStorage.setItem("token", token);
    this.loggedIn.next(true); // 🔥 triggers UI update
  } 

  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem("token");
    }
    return null;
  }

  logout() {
    localStorage.removeItem("token");
    localStorage.removeItem('email');
    this.loggedIn.next(false);
  }

  isLogged(): boolean {
    return !!this.getToken();
  }

  // Retrieve email from localStorage
  /*
  getEmail(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('email');
    }
    return null;
  }
    */




}