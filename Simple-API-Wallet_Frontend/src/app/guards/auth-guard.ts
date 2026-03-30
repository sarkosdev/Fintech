import { CanActivateFn, Router } from '@angular/router';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export const authGuard: CanActivateFn = (route, state) => {

  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  function isTokenExpired(token: string) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiry = payload.exp * 1000;
      return Date.now() > expiry;
    } catch (e) {
      return true; // If token is malformed, consider it expired
    }
    
  }

  let token: string | null = null;
  

  if (isPlatformBrowser(platformId)) {
    token = localStorage.getItem('token'); // ✅ SAFE
  }

  if (token && !isTokenExpired(token)) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};