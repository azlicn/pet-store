import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const userProfileGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  // First check if user is logged in
  if (!authService.isLoggedIn()) {
    router.navigate(['/unauthorized'], {
      queryParams: {
        type: 'not-logged-in',
        returnUrl: state.url
      }
    });
    return false;
  }
  
  // Get the user ID from the route parameters
  const requestedUserId = parseInt(route.paramMap.get('id') || '0');
  const currentUser = authService.getCurrentUser();
  
  // Allow access if:
  // 1. User is admin (can edit anyone)
  // 2. User is editing their own profile
  if (authService.isAdmin() || (currentUser && currentUser.id === requestedUserId)) {
    return true;
  }
  
  // User is trying to edit someone else's profile
  router.navigate(['/unauthorized'], {
    queryParams: {
      type: 'forbidden',
      returnUrl: state.url
    }
  });
  
  return false;
};