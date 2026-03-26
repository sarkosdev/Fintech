import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {

  const router = inject(Router);

  function isTokenExpired(token: string) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiry = payload.exp * 1000;
      return Date.now() > expiry;
    } catch (e) {
      return true; // If token is malformed, consider it expired
    }
    
  }

  const token = localStorage.getItem('token');
  

  if (token && !isTokenExpired(token)) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};