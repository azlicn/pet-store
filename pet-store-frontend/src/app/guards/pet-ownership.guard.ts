import { inject } from "@angular/core";
import { CanActivateFn, Router, ActivatedRouteSnapshot } from "@angular/router";
import { AuthService } from "../services/auth.service";
import { PetService } from "../services/pet.service";
import { map, catchError } from "rxjs/operators";
import { of } from "rxjs";

export const petOwnershipGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state
) => {
  const authService = inject(AuthService);
  const petService = inject(PetService);
  const router = inject(Router);

  // First check if user is logged in
  if (!authService.isLoggedIn()) {
    router.navigate(["/unauthorized"], {
      queryParams: {
        type: "not-logged-in",
        returnUrl: state.url,
      },
    });
    return false;
  }

  // Get the pet ID from the route parameters
  const petId = parseInt(route.paramMap.get("id") || "0");
  const currentUser = authService.getCurrentUser();

  if (!currentUser || !petId) {
    router.navigate(["/unauthorized"], {
      queryParams: {
        type: "forbidden",
        returnUrl: state.url,
      },
    });
    return false;
  }

  // Admins can edit any pet
  if (authService.isAdmin()) {
    return true;
  }

  // For regular users, check if they own the pet
  return petService.getPetById(petId).pipe(
    map((pet) => {
      // Check if the current user is the owner of the pet
      if (pet.createdBy === currentUser.id) {
        return true;
      } else {
        // User doesn't own this pet
        router.navigate(["/unauthorized"], {
          queryParams: {
            type: "forbidden",
            returnUrl: state.url,
          },
        });
        return false;
      }
    }),
    catchError((error) => {
      // If pet doesn't exist or other error, redirect to unauthorized
      console.error("Error checking pet ownership:", error);
      router.navigate(["/unauthorized"], {
        queryParams: {
          type: "forbidden",
          returnUrl: state.url,
        },
      });
      return of(false);
    })
  );
};
