import { ErrorHandler, Injectable } from '@angular/core';

/**
 * Global error handler for the application
 * Note: Cart endpoints no longer return 404, so no special handling needed
 */
@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  
  handleError(error: any): void {
    // Log all errors to console
    console.error('An error occurred:', error);
  }
}
