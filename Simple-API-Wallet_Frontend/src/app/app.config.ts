import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withInMemoryScrolling } from '@angular/router';
import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

// PrimeNG Dynamic Theme
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeuix/themes/aura'; // Example theme preset
import { jwtInterceptor } from './interceptors/jwt-interceptor';


export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(
      routes,
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
        anchorScrolling: 'enabled'
      })),
    provideClientHydration(withEventReplay()),
    provideAnimations(),
    provideAnimationsAsync(),
    provideHttpClient(withInterceptors([jwtInterceptor])),

    // Add PrimeNG provider here
    providePrimeNG({
      theme: { preset: Aura },  // <-- Dynamic theme applied
      ripple: true,             // optional: enable ripple globally
    })
  ]
};
