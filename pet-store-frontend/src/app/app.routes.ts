import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './guards/auth.guard';
import { userProfileGuard } from './guards/user-profile.guard';
import { petOwnershipGuard } from './guards/pet-ownership.guard';

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
    path: 'unauthorized',
    loadComponent: () => import('./components/unauthorized/unauthorized.component').then(m => m.UnauthorizedComponent)
  },
  {
    path: 'docs',
    loadComponent: () => import('./components/documentation/documentation.component').then(m => m.DocumentationComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];