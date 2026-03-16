import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ILoginRequest } from '../model/login.model';
import { isPlatformBrowser } from '@angular/common';
import { IRegisterRequest } from '../model/register.model';
import { IApiResponse } from '../model/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

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
    if (typeof window !== 'undefined') {
      localStorage.setItem("token", token);
    }
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
    }
  }

  isLogged(): boolean {
    return !!this.getToken();
  }
}