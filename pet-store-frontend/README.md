# 🎨 Pawfect Store - Frontend

Angular 17 frontend application for Pawfect Store pet e-commerce platform.

## 📋 Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Building for Production](#building-for-production)
- [Testing](#testing)
- [Components](#components)
- [Services](#services)
- [Routing & Guards](#routing--guards)
- [Styling](#styling)

---

## 🎯 Overview

The Pawfect Store frontend is a modern, responsive web application built with Angular 17, featuring:

- **Standalone Components** - Angular 17's latest component architecture
- **Angular Material** - Material Design UI components
- **RxJS** - Reactive programming with observables
- **JWT Authentication** - Secure token-based authentication
- **Role-Based UI** - Different views for users and admins
- **Responsive Design** - Mobile-friendly interface
- **Lazy Loading** - Optimized performance with route-based code splitting

---

## 🛠️ Technology Stack

- **Framework**: Angular 17
- **UI Library**: Angular Material 17
- **Language**: TypeScript 5.2+
- **State Management**: Services with RxJS
- **HTTP Client**: Angular HttpClient
- **Routing**: Angular Router
- **Forms**: Reactive Forms
- **Icons**: Material Icons
- **Build Tool**: Angular CLI
- **Package Manager**: npm

---

## ✅ Prerequisites

- **Node.js 18+** (LTS recommended)
- **npm 9+** (comes with Node.js)
- **Angular CLI 17+**

Install Angular CLI globally:
```bash
npm install -g @angular/cli@17
```

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/azlicn/pet-store.git
cd pet-store/pet-store-frontend
```

### 2. Install Dependencies

```bash
npm install
```

### 3. Configure Environment

Update `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

### 4. Start Development Server

```bash
npm start
# or
ng serve
```

The application will be available at `http://localhost:4200`

---

## 📁 Project Structure

```
pet-store-frontend/
├── src/
│   ├── app/
│   │   ├── components/              # Angular components
│   │   │   ├── address/             # Address form component
│   │   │   ├── address-book/        # Address list management
│   │   │   ├── cart/                # Shopping cart
│   │   │   ├── cart-overlay/        # Cart dropdown
│   │   │   ├── category-form/       # Category CRUD
│   │   │   ├── category-list/       # Category management
│   │   │   ├── checkout/            # Checkout process
│   │   │   ├── confirm-dialog/      # Confirmation dialogs
│   │   │   ├── discount-form/       # Discount CRUD
│   │   │   ├── discount-list/       # Discount management
│   │   │   ├── header/              # Navigation header
│   │   │   ├── home/                # Landing page
│   │   │   ├── login/               # Login form
│   │   │   ├── order-card/          # Order display card
│   │   │   ├── order-history/       # User order history
│   │   │   ├── order-list/          # Admin order management
│   │   │   ├── payment-processing-dialog/  # Payment modal
│   │   │   ├── pet-card/            # Pet display card
│   │   │   ├── pet-form/            # Pet CRUD form
│   │   │   ├── pet-list/            # Pet browsing
│   │   │   ├── register/            # Registration form
│   │   │   ├── user-edit/           # User profile editing
│   │   │   ├── user-list/           # User management
│   │   │   └── unauthorized/        # 403 page
│   │   ├── guards/                  # Route guards
│   │   │   ├── auth.guard.ts        # Authentication guard
│   │   │   ├── pet-ownership.guard.ts  # Ownership validation
│   │   │   └── user-profile.guard.ts   # Profile access control
│   │   ├── interceptors/            # HTTP interceptors
│   │   │   ├── auth.interceptor.ts       # JWT token injection
│   │   │   └── unauthorized.interceptor.ts  # 401 handling
│   │   ├── models/                  # TypeScript interfaces
│   │   │   ├── address.model.ts
│   │   │   ├── category.model.ts
│   │   │   ├── discount.model.ts
│   │   │   ├── order.model.ts
│   │   │   ├── payment.model.ts
│   │   │   └── pet.model.ts
│   │   ├── services/                # HTTP services
│   │   │   ├── address.service.ts
│   │   │   ├── auth.service.ts
│   │   │   ├── base-api.service.ts
│   │   │   ├── category.service.ts
│   │   │   ├── discount.service.ts
│   │   │   ├── error-handler.service.ts
│   │   │   ├── pet.service.ts
│   │   │   ├── store.service.ts
│   │   │   └── user.service.ts
│   │   ├── app.component.ts         # Root component
│   │   ├── app.config.ts            # App configuration
│   │   └── app.routes.ts            # Route definitions
│   ├── environments/                # Environment configs
│   │   ├── environment.ts           # Development
│   │   ├── environment.prod.ts      # Production
│   │   └── environment.docker.ts    # Docker
│   ├── assets/                      # Static assets
│   │   └── images/                  # Images
│   ├── styles.scss                  # Global styles
│   ├── index.html                   # Entry HTML
│   └── main.ts                      # Bootstrap file
├── angular.json                     # Angular CLI config
├── tsconfig.json                    # TypeScript config
├── package.json                     # Dependencies
├── Dockerfile                       # Docker build
└── nginx.conf                       # Production nginx config
```

---

## ⚙️ Configuration

### Environment Files

#### Development (`environment.ts`)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

#### Production (`environment.prod.ts`)
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.pawfectstore.com/api'
};
```

#### Docker (`environment.docker.ts`)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

### Angular Configuration

Key settings in `angular.json`:
```json
{
  "projects": {
    "pet-store-frontend": {
      "architect": {
        "build": {
          "configurations": {
            "production": {
              "fileReplacements": [{
                "replace": "src/environments/environment.ts",
                "with": "src/environments/environment.prod.ts"
              }]
            }
          }
        }
      }
    }
  }
}
```

---

## 🏃 Running the Application

### Development Server
```bash
npm start
# or
ng serve

# With specific port
ng serve --port 4200

# With proxy configuration
ng serve --proxy-config proxy.conf.json
```

### Development with Auto-Reload
The development server automatically reloads on file changes.

### With VS Code
Use the configured tasks:
1. Press `Ctrl+Shift+P` (Cmd+Shift+P on Mac)
2. Select "Tasks: Run Task"
3. Choose "Start Frontend"

---

## 🏗️ Building for Production

### Build
```bash
npm run build
# or
ng build --configuration production
```

Output: `dist/pet-store-frontend/`

### Build with Docker
```bash
docker build -t pet-store-frontend .
docker run -p 80:80 pet-store-frontend
```

### Production Optimizations
- **Ahead-of-Time (AOT) Compilation**
- **Tree Shaking** - Remove unused code
- **Minification** - Reduce file sizes
- **Lazy Loading** - Load modules on demand
- **Service Worker** - PWA capabilities (optional)

---

## 🧪 Testing

### Unit Tests
```bash
npm test
# or
ng test
```

### Unit Tests (Headless)
```bash
ng test --browsers=ChromeHeadless --watch=false
```

### E2E Tests
```bash
npm run e2e
# or
ng e2e
```

### Code Coverage
```bash
ng test --code-coverage
```

View coverage report: `coverage/pet-store-frontend/index.html`

### Test Structure
```
src/
└── app/
    ├── components/
    │   └── pet-list/
    │       ├── pet-list.component.ts
    │       └── pet-list.component.spec.ts  # Unit tests
    └── services/
        ├── pet.service.ts
        └── pet.service.spec.ts  # Service tests
```

---

## 🧩 Components

### Core Components

#### Header Component
Navigation bar with authentication status and cart icon.

```typescript
@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, MatToolbarModule, MatButtonModule],
  templateUrl: './header.component.html'
})
export class HeaderComponent {
  isLoggedIn$ = this.authService.isLoggedIn$;
  currentUser$ = this.authService.currentUser$;
  
  constructor(private authService: AuthService) {}
  
  logout(): void {
    this.authService.logout();
  }
}
```

#### Pet Card Component
Reusable card for displaying pet information.

```typescript
@Component({
  selector: 'app-pet-card',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <mat-card>
      <img mat-card-image [src]="pet.imageUrl" [alt]="pet.name">
      <mat-card-content>
        <h3>{{ pet.name }}</h3>
        <p>{{ pet.price | currency }}</p>
      </mat-card-content>
    </mat-card>
  `
})
export class PetCardComponent {
  @Input() pet!: Pet;
}
```

### Feature Components

- **Home** - Landing page with featured pets
- **Pet List** - Browse all pets with filters
- **Pet Form** - Add/edit pet listings
- **Cart** - Shopping cart management
- **Checkout** - Order checkout process
- **Order History** - View past orders
- **User Profile** - Edit user information

### Admin Components

- **Category Management** - CRUD operations for categories
- **Discount Management** - Manage discount codes
- **User Management** - Manage system users
- **Order Management** - View and update all orders

---

## 🔌 Services

### Auth Service
Handles authentication and authorization.

```typescript
@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();
  
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(response => {
          localStorage.setItem('token', response.token);
          this.currentUserSubject.next(response.user);
        })
      );
  }
  
  logout(): void {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }
  
  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }
}
```

### Pet Service
Manages pet-related API calls.

```typescript
@Injectable({ providedIn: 'root' })
export class PetService extends BaseApiService {
  private petsUrl = `${this.apiUrl}/pets`;
  
  getAllPets(params?: any): Observable<PetPageResponse> {
    return this.http.get<PetPageResponse>(this.petsUrl, { params });
  }
  
  getPetById(id: number): Observable<Pet> {
    return this.http.get<Pet>(`${this.petsUrl}/${id}`);
  }
  
  createPet(pet: Pet): Observable<Pet> {
    return this.http.post<Pet>(this.petsUrl, pet);
  }
  
  updatePet(id: number, pet: Pet): Observable<Pet> {
    return this.http.put<Pet>(`${this.petsUrl}/${id}`, pet);
  }
  
  deletePet(id: number): Observable<void> {
    return this.http.delete<void>(`${this.petsUrl}/${id}`);
  }
}
```

### Store Service
Handles cart, orders, and payments.

```typescript
@Injectable({ providedIn: 'root' })
export class StoreService {
  addToCart(petId: number): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/stores/cart/add`, { petId });
  }
  
  checkout(checkoutRequest: CheckoutRequest): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/stores/checkout`, checkoutRequest);
  }
  
  makePayment(orderId: number, paymentRequest: PaymentRequest): Observable<Payment> {
    return this.http.post<Payment>(
      `${this.apiUrl}/stores/order/${orderId}/pay`,
      paymentRequest
    );
  }
}
```

---

## 🛣️ Routing & Guards

### Route Configuration

```typescript
export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'pets',
    component: PetListComponent,
    canActivate: [authGuard]
  },
  {
    path: 'pets/:id/edit',
    component: PetFormComponent,
    canActivate: [authGuard, petOwnershipGuard]
  },
  {
    path: 'cart',
    component: CartComponent,
    canActivate: [authGuard]
  },
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    children: [
      { path: 'categories', component: CategoryListComponent },
      { path: 'discounts', component: DiscountListComponent },
      { path: 'users', component: UserListComponent }
    ]
  },
  { path: 'unauthorized', component: UnauthorizedComponent },
  { path: '**', redirectTo: '' }
];
```

### Auth Guard

```typescript
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  if (authService.isAuthenticated()) {
    return true;
  }
  
  router.navigate(['/login'], {
    queryParams: { returnUrl: state.url }
  });
  return false;
};
```

### Admin Guard

```typescript
export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  if (authService.isAdmin()) {
    return true;
  }
  
  router.navigate(['/unauthorized']);
  return false;
};
```

---

## 🎨 Styling

### Global Styles

Located in `src/styles.scss`:

```scss
@import '@angular/material/prebuilt-themes/indigo-pink.css';

:root {
  --primary-color: #3f51b5;
  --accent-color: #ff4081;
  --warn-color: #f44336;
}

body {
  margin: 0;
  font-family: Roboto, "Helvetica Neue", sans-serif;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
```

### Component Styles

Each component has its own SCSS file:

```scss
// pet-card.component.scss
.pet-card {
  max-width: 300px;
  margin: 10px;
  
  img {
    height: 200px;
    object-fit: cover;
  }
  
  .price {
    font-size: 1.2em;
    font-weight: bold;
    color: var(--primary-color);
  }
}
```

### Material Theming

Custom theme in `src/styles.scss`:

```scss
@use '@angular/material' as mat;

$my-primary: mat.define-palette(mat.$indigo-palette);
$my-accent: mat.define-palette(mat.$pink-palette, A200, A100, A400);
$my-theme: mat.define-light-theme((
  color: (
    primary: $my-primary,
    accent: $my-accent,
  )
));

@include mat.all-component-themes($my-theme);
```

---

## 🚀 Deployment

### Docker Deployment

```bash
# Build image
docker build -t pet-store-frontend .

# Run container
docker run -p 80:80 pet-store-frontend
```

### Production Build

```bash
npm run build
# Files in dist/pet-store-frontend/
```

Serve with nginx using `nginx.conf`.

For detailed deployment instructions, see [Deployment Guide](../docs/deployment.md).

---

## 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guide](../CONTRIBUTING.md) for details.

---

## 📄 License

This project is licensed under the MIT License.

---

## 📚 Additional Resources

- [Main Documentation](../README.md)
- [Architecture Overview](../docs/architecture.md)
- [API Documentation](../docs/api.md)
- [Setup Guide](../docs/setup.md)
- [Angular Documentation](https://angular.io/docs)
- [Angular Material](https://material.angular.io/)

---

**Made with ❤️ by the Pawfect Store Team**
