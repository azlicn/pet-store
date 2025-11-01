import { ApplicationConfig, ErrorHandler } from '@angular/core';
import { MatNativeDateModule } from '@angular/material/core';
import { importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

import { routes } from './app.routes';
import { AuthService } from './services/auth.service';
import { GlobalErrorHandler } from './handlers/global-error.handler';

// Create functional auth interceptor
const authInterceptor = (req: any, next: any) => {
  const authService = inject(AuthService);
  const token = authService.getToken();
  
  if (token) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(authReq);
  }
  
  return next(req);
};

// Create functional unauthorized interceptor
const unauthorizedInterceptor = (req: any, next: any) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Unauthorized - user needs to log in or token is invalid
        authService.logout(); // Clear any invalid tokens
        router.navigate(['/unauthorized'], {
          queryParams: {
            type: 'not-logged-in',
            returnUrl: router.url
          }
        });
      } else if (error.status === 403) {
        // Forbidden - user doesn't have permission for this specific resource
        const errorMessage = error.error?.message || '';
        let forbiddenType = 'forbidden';
        
        // Check if this is a user profile or pet ownership access error
        if (errorMessage.includes('own profile') || (req.url.includes('/users/') && req.method !== 'GET')) {
          forbiddenType = 'forbidden';
        } else if (errorMessage.includes('own pet') || (req.url.includes('/pets/') && req.method !== 'GET')) {
          forbiddenType = 'forbidden';
        }
        
        router.navigate(['/unauthorized'], {
          queryParams: {
            type: forbiddenType,
            returnUrl: router.url
          }
        });
      }
      
      return throwError(() => error);
    })
  );
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor, unauthorizedInterceptor])),
    provideAnimationsAsync(),
    importProvidersFrom(MatNativeDateModule),
    { provide: ErrorHandler, useClass: GlobalErrorHandler }
  ]
};