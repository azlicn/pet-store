import { Injectable, inject } from "@angular/core";
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpErrorResponse,
} from "@angular/common/http";
import { Router } from "@angular/router";
import { catchError } from "rxjs/operators";
import { throwError } from "rxjs";
import { AuthService } from "../services/auth.service";

@Injectable()
export class UnauthorizedInterceptor implements HttpInterceptor {
  private router = inject(Router);
  private authService = inject(AuthService);

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Unauthorized - user needs to log in
          this.authService.logout(); // Clear any invalid tokens
          this.router.navigate(["/unauthorized"], {
            queryParams: {
              type: "not-logged-in",
              returnUrl: this.router.url,
            },
          });
        } else if (error.status === 403) {
          // Forbidden - user doesn't have permission
          this.router.navigate(["/unauthorized"], {
            queryParams: {
              type: "forbidden",
              returnUrl: this.router.url,
            },
          });
        }

        return throwError(() => error);
      })
    );
  }
}
