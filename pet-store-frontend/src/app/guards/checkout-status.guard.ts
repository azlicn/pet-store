import { inject } from "@angular/core";
import { CanActivateFn, Router, ActivatedRouteSnapshot } from "@angular/router";
import { StoreService } from "../services/store.service";
import { map, catchError } from "rxjs/operators";
import { of } from "rxjs";

export const checkoutStatusGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state
) => {
  console.log("checkoutStatusGuard called", route.params);
  const storeService = inject(StoreService);
  const router = inject(Router);
  const orderId = +(route.params["orderId"] || route.params["id"]);
  return storeService.getOrder(orderId).pipe(
    map((order) =>
      order.status === "PLACED" ? true : router.createUrlTree(["/orders"])
    ),
    catchError(() => of(router.createUrlTree(["/orders"])))
  );
};
