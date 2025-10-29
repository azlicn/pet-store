import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './guards/auth.guard';
import { userProfileGuard } from './guards/user-profile.guard';
import { petOwnershipGuard } from './guards/pet-ownership.guard';
import { checkoutStatusGuard } from './guards/checkout-status.guard';
import { OrderHistoryComponent } from './components/order-history/order-history.component';
import { orderOwnershipGuard } from './guards/order-ownership.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'home',
    redirectTo: '',
    pathMatch: 'full'
  },
   {
    path: 'address-book',
    loadComponent: () => import('./components/address-book/address-book.component').then(m => m.AddressBookComponent)
  },
  {
    path: 'pets',
    loadComponent: () => import('./components/pet-list/pet-list.component').then(m => m.PetListComponent)
  },
  {
    path: 'pets/add',
    loadComponent: () => import('./components/pet-form/pet-form.component').then(m => m.PetFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'pets/edit/:id',
    loadComponent: () => import('./components/pet-form/pet-form.component').then(m => m.PetFormComponent),
    canActivate: [petOwnershipGuard]
  },
  {
    path: 'login',
    loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./components/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'users',
    loadComponent: () => import('./components/user-list/user-list.component').then(m => m.UserListComponent),
    canActivate: [adminGuard]
  },
  {
    path: 'users/:id/edit',
    loadComponent: () => import('./components/user-edit/user-edit.component').then(m => m.UserEditComponent),
    canActivate: [userProfileGuard]
  },
  {
    path: 'categories',
    loadComponent: () => import('./components/category-list/category-list.component').then(m => m.CategoryListComponent),
    canActivate: [adminGuard]
  },
  {
    path: 'categories/add',
    loadComponent: () => import('./components/category-form/category-form.component').then(m => m.CategoryFormComponent),
    canActivate: [adminGuard]
  },
  {
    path: 'categories/edit/:id',
    loadComponent: () => import('./components/category-form/category-form.component').then(m => m.CategoryFormComponent),
    canActivate: [adminGuard]
  },
  {
    path: 'discounts',
    loadComponent: () => import('./components/discount-list/discount-list.component').then(m => m.DiscountListComponent),
    canActivate: [adminGuard]
  },
  {
    path: 'discounts/add',
    loadComponent: () => import('./components/discount-form/discount-form.component').then(m => m.DiscountFormComponent),
    canActivate: [adminGuard]
  },
  {
    path: 'discounts/edit/:id',
    loadComponent: () => import('./components/discount-form/discount-form.component').then(m => m.DiscountFormComponent),
    canActivate: [adminGuard]
  },
  {
    path: 'orders',
    loadComponent: () => import('./components/order-list/order-list.component').then(m => m.OrderListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'checkout/:orderId',
    loadComponent: () => import('./components/checkout/checkout.component').then(m => m.CheckoutComponent),
    canActivate: [authGuard, checkoutStatusGuard]
  },
  {
    path: 'unauthorized',
    loadComponent: () => import('./components/unauthorized/unauthorized.component').then(m => m.UnauthorizedComponent)
  },
  {
    path: 'docs',
    loadComponent: () => import('./components/documentation/documentation.component').then(m => m.DocumentationComponent)
  },
  {
    path: 'order-history/:orderId',
    loadComponent: () => import('./components/order-history/order-history.component').then(m => m.OrderHistoryComponent),
    canActivate: [orderOwnershipGuard]
  },
  {
    path: '**',
    redirectTo: ''
  }
];