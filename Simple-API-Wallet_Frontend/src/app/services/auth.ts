import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { ILoginRequest, ILoginResponse } from '../model/login.model';
import { isPlatformBrowser } from '@angular/common';
import { IRegisterRequest } from '../model/register.model';
import { IApiResponse } from '../model/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private loggedIn = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.loggedIn.asObservable();

 //backend api url
  private api = 'http://localhost:8080/api/fintech/auth';

  platformId = inject(PLATFORM_ID);

  constructor(private http: HttpClient) {
    if (isPlatformBrowser(this.platformId)) {
      this.loggedIn.next(this.hasToken());
    }
  }

  login(loginRequest: ILoginRequest): Observable<ILoginResponse> {
    return this.http.post<ILoginResponse>(`${this.api}/login`, loginRequest);
  }

  register(registerRequest: IRegisterRequest): Observable<IApiResponse> {
    return this.http.post<IApiResponse>(`${this.api}/register`, registerRequest);
  }

  saveToken(token: string) {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem("token", token);
    }
    this.loggedIn.next(true);
  }

  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem("token");
    }
    return null;
  }

  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem("token");
      localStorage.removeItem("userEmail");
    }
    this.loggedIn.next(false);
  }

  isLogged(): boolean {
    return !!this.getToken();
  }

  // Retrieve email from localStorage
  getEmail(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem("userEmail");
    }
    return null;
  }

  hasToken(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return !!localStorage.getItem("token");
    }
    return false;
  }




}