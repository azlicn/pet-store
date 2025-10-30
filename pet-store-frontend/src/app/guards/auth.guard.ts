import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  // Redirect to unauthorized page with login required type
  router.navigate(["/unauthorized"], {
    queryParams: {
      type: "not-logged-in",
      returnUrl: state.url,
    },
  });

  return false;
};

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    // User not logged in
    router.navigate(["/unauthorized"], {
      queryParams: {
        type: "not-logged-in",
        returnUrl: state.url,
      },
    });
    return false;
  }

  if (authService.isAdmin()) {
    return true;
  }

  // User logged in but not admin
  router.navigate(["/unauthorized"], {
    queryParams: {
      type: "forbidden",
      returnUrl: state.url,
    },
  });

  return false;
};
