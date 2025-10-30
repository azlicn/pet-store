import { CanActivateFn } from "@angular/router";
import { inject } from "@angular/core";
import { StoreService } from "../services/store.service";
import { AuthService } from "../services/auth.service";
import { Router } from "@angular/router";
import { of } from "rxjs";
import { catchError, map } from "rxjs/operators";

export const orderOwnershipGuard: CanActivateFn = (route) => {
  const storeService = inject(StoreService);
  const authService = inject(AuthService);
  const router = inject(Router);
  const orderId = Number(route.paramMap.get("orderId"));
  const currentUser = authService.getCurrentUser();
  const currentUserId = currentUser ? currentUser.id : null;

  return storeService.getOrder(orderId).pipe(
    map((order) => {
      if (order.user.id === currentUserId || authService.isAdmin()) {
        return true;
      } else {
        router.navigate(["/unauthorized"]);
        return false;
      }
    }),
    catchError(() => {
      router.navigate(["/unauthorized"]);
      return of(false);
    })
  );
};
