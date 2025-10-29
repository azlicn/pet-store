/**
 * Standard error response interface matching the backend ErrorResponse format
 */
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  code: string;
}

/**
 * HTTP error response wrapper
 */
export interface HttpErrorResponse {
  error: ErrorResponse;
  status: number;
  statusText: string;
  message?: string;
}