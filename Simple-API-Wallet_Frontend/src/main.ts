import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { appConfig } from './app/app.config';
import { provideHttpClient, withFetch } from '@angular/common/http';


bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));

provideHttpClient(withFetch());

