import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '../models/error-response.model';

/**
 * Utility service for handling API errors and extracting user-friendly messages
 */
@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {

  /**
   * Extract a user-friendly error message from an HTTP error response
   */
  extractErrorMessage(error: HttpErrorResponse, defaultMessage: string = 'An error occurred'): string {
    // Handle specific status codes
    switch (error.status) {
      case 0:
        return 'Unable to connect to server. Please check your internet connection.';
      
      case 401:
        return 'Please log in to continue.';
      
      case 403:
        return 'You do not have permission to perform this action.';
      
      case 404:
        return 'The requested resource was not found.';
      
      case 409:
        // Conflict errors usually have specific business logic messages
        return error.error?.message || 'A conflict occurred with your request.';
      
      case 422:
        return error.error?.message || 'The data provided is invalid.';
      
      case 500:
        return 'A server error occurred. Please try again later.';
      
      case 503:
        return 'The service is temporarily unavailable. Please try again later.';
      
      default:
        // Try to extract message from error response
        if (error.error?.message) {
          return error.error.message;
        }
        
        if (error.message) {
          return error.message;
        }
        
        return defaultMessage;
    }
  }

  /**
   * Get appropriate snackbar duration based on error severity
   */
  getErrorDuration(error: HttpErrorResponse): number {
    switch (error.status) {
      case 409: // Conflict errors are important business rules
      case 422: // Validation errors need time to read
        return 8000;
      
      case 401:
      case 403:
        return 6000;
      
      default:
        return 5000;
    }
  }

  /**
   * Determine if an error should auto-dismiss or require user action
   */
  shouldAutoDismiss(error: HttpErrorResponse): boolean {
    // Authentication errors might need user action
    return error.status !== 401;
  }
}