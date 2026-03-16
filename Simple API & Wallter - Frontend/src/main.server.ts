import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { config } from './app/app.config.server';

const bootstrap = () => bootstrapApplication(App, config);


export default function () {
  return bootstrapApplication(App, {
    ...config,
    providers: [
      ...(config.providers ?? [])
    ]
  });
}