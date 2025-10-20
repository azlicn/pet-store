import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MermaidDiagramComponent } from '../mermaid-diagram/mermaid-diagram.component';

interface DiagramData {
  id: string;
  title: string;
  description: string;
  definition: string;
  category: 'architecture' | 'database' | 'api' | 'api-section' | 'user-flow' | 'deployment';
}

@Component({
  selector: 'app-documentation',
  standalone: true,
  imports: [
    CommonModule,
    MatTabsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MermaidDiagramComponent
  ],
  templateUrl: './documentation.component.html',
  styleUrls: ['./documentation.component.scss']
})
export class DocumentationComponent implements OnInit, OnDestroy {
  currentDate = new Date().toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });

  showBackToTop = false;

  trackByDiagram(index: number, diagram: DiagramData): string {
    return diagram.id;
  }

  scrollToSection(sectionId: string): void {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ 
        behavior: 'smooth', 
        block: 'start',
        inline: 'nearest'
      });
    }
  }

  scrollToTop(): void {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  }

  // Listen for scroll events to show/hide back to top button
  @HostListener('window:scroll', [])
  onWindowScroll(): void {
    const scrollPosition = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
    this.showBackToTop = scrollPosition > 300; // Show button after scrolling 300px
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
  }

  private diagrams: DiagramData[] = [
    {
      id: 'system-overview',
      title: 'System Overview Architecture',
      description: 'High-level view of the 3-tier Pet Store application showing frontend, backend, and database layers with their interactions.',
      category: 'architecture',
      definition: `graph TB
    subgraph "Frontend Layer"
        A[Angular 17 App<br/>Port: 4200]
        A1[Components]
        A2[Services]
        A3[Guards]
        A --> A1
        A --> A2
        A --> A3
    end

    subgraph "Backend Layer"
        B[Spring Boot API<br/>Port: 8080]
        B1[Controllers]
        B2[Services]
        B3[Repositories]
        B4[Security]
        B --> B1
        B --> B2
        B --> B3
        B --> B4
    end

    subgraph "Database Layer"
        C[(MySQL Database<br/>Port: 3306)]
        C1[Pet Table]
        C2[Category Table]
        C3[User Table]
        C --> C1
        C --> C2
        C --> C3
    end

    A1 -.->|HTTP/HTTPS| B1
    A2 -.->|REST API| B1
    B2 --> B3
    B3 -.->|JPA/Hibernate| C
    B4 -.->|JWT Auth| A3

    classDef frontend fill:#e1f5fe
    classDef backend fill:#f3e5f5
    classDef database fill:#e8f5e8
    
    class A,A1,A2,A3 frontend
    class B,B1,B2,B3,B4 backend
    class C,C1,C2,C3 database`
    },
    {
      id: 'component-architecture',
      title: 'Component Architecture',
      description: 'Detailed view of application components and their relationships within each layer.',
      category: 'architecture',
      definition: `graph LR
    subgraph "Angular Frontend Components"
        HC[HomeComponent]
        PLC[PetListComponent]
        PFC[PetFormComponent]
        LC[LoginComponent]
        PCC[PetCardComponent]
        
        HC --> PLC
        PLC --> PCC
        PFC --> PS[PetService]
        LC --> AS[AuthService]
    end

    subgraph "Spring Boot Backend"
        PC[PetController]
        PS2[PetService]
        PR[PetRepository]
        
        PC --> PS2
        PS2 --> PR
    end

    subgraph "Database Entities"
        PE[Pet Entity]
        CE[Category Entity]
        UE[User Entity]
    end

    PS -.->|HTTP Calls| PC
    AS -.->|Authentication| PC
    PR -.->|JPA Mapping| PE
    PE -.->|ManyToOne| CE

    classDef component fill:#bbdefb
    classDef service fill:#c8e6c9
    classDef controller fill:#ffccbc
    classDef entity fill:#f8bbd9

    class HC,PLC,PFC,LC,PCC component
    class PS,AS,PS2 service
    class PC controller
    class PE,CE,UE entity`
    },
    {
      id: 'database-schema',
      title: 'Database Entity Relationships',
      description: 'Entity Relationship Diagram showing the actual database schema with tables, relationships, and key constraints based on JPA entities.',
      category: 'database',
      definition: `erDiagram
    pets {
        bigint id PK
        string name "NOT NULL"
        decimal price "NOT NULL, PRECISION(10,2)"
        string status "ENUM: AVAILABLE, PENDING, SOLD"
        bigint category_id FK "NOT NULL"
        bigint user_id FK "NULLABLE - Pet Owner"
        datetime created_at "Audit Field"
        datetime updated_at "Audit Field"
        bigint created_by "Audit Field"
        bigint last_modified_by "Audit Field"
    }
    
    categories {
        bigint id PK
        string name "NOT NULL, UNIQUE"
        datetime created_at
        datetime updated_at
    }
    
    users {
        bigint id PK
        string email "NOT NULL, UNIQUE"
        string password "NOT NULL, MIN 6 chars"
        string first_name "NOT NULL"
        string last_name "NOT NULL"
        datetime created_at
        datetime updated_at
    }
    
    pet_photos {
        bigint pet_id FK
        string photo_url "ElementCollection"
    }
    
    pet_tags {
        bigint pet_id FK
        string tag "ElementCollection"
    }
    
    user_roles {
        bigint user_id FK
        string role "ENUM: USER, ADMIN"
    }

    categories ||--o{ pets : "category_id"
    users ||--o{ pets : "user_id (owner)"
    pets ||--o{ pet_photos : "pet_id"
    pets ||--o{ pet_tags : "pet_id"
    users ||--o{ user_roles : "user_id"`
    },
    {
      id: 'authentication-sequence-title',
      title: 'Authentication Sequence Diagrams',
      description: 'The following sequence diagrams detail the authentication and user registration processes in the Pet Store API. These diagrams show JWT token generation, validation, user registration with role assignment, and protected resource access patterns.',
      category: 'api-section',
      definition: ''
    },
    {
      id: 'user-login-flow',
      title: 'User Login Authentication (POST)',
      description: 'User login process with JWT token generation and validation - includes password verification and session management.',
      category: 'api',
      definition: `sequenceDiagram
    participant U as User
    participant F as Frontend
    participant AC as AuthController
    participant AM as AuthenticationManager
    participant UR as UserRepository
    participant JWT as JwtTokenProvider
    participant PE as PasswordEncoder
    participant DB as Database

    U->>F: Enter email/password
    F->>AC: POST /api/auth/login<br/>{email, password}
    AC->>AM: authenticate(UsernamePasswordAuthenticationToken)
    AM->>UR: findByEmail(email)
    UR->>DB: SELECT * FROM users WHERE email = ?
    DB-->>UR: User entity with encoded password
    UR-->>AM: User details + roles
    AM->>PE: matches(plainPassword, encodedPassword)
    PE-->>AM: Password validation result
    
    alt Valid Credentials
        AM-->>AC: Authentication object with authorities
        AC->>JWT: generateToken(authentication)
        JWT->>JWT: Create JWT with HMAC-SHA512 + user claims
        JWT-->>AC: JWT token string
        AC->>UR: findByEmail(email) for response
        UR-->>AC: User entity
        AC-->>F: 200 OK {token: "Bearer xyz", user: {id, email, firstName, lastName, roles}}
        F->>F: Store token in sessionStorage
        F->>F: Store user data in sessionStorage
        F-->>U: Redirect to dashboard/pet listing
    else Invalid Credentials
        AM-->>AC: Authentication failed
        AC-->>F: 400 Bad Request {"message": "Invalid email or password"}
        F-->>U: Show error message
    end`
    },
    {
      id: 'user-registration-flow',
      title: 'User Registration (POST)',
      description: 'New user account creation with email validation, password encoding, and role assignment.',
      category: 'api',
      definition: `sequenceDiagram
    participant U as User
    participant F as Frontend
    participant AC as AuthController
    participant UR as UserRepository
    participant PE as PasswordEncoder
    participant DB as Database

    U->>F: Fill registration form
    F->>AC: POST /api/auth/register<br/>{firstName, lastName, email, password, role}
    AC->>UR: existsByEmail(email)
    UR->>DB: SELECT COUNT(*) FROM users WHERE email = ?
    
    alt Email Already Exists
        UR-->>AC: true (email taken)
        AC-->>F: 400 Bad Request {"message": "Email is already in use!"}
        F-->>U: Show email already exists error
    else Email Available
        UR-->>AC: false (email available)
        AC->>PE: encode(password)
        PE-->>AC: Encrypted password hash
        AC->>AC: Create User entity with encrypted password
        AC->>AC: Set role (default: USER, optional: ADMIN)
        AC->>UR: save(newUser)
        UR->>DB: INSERT INTO users<br/>(first_name, last_name, email, password, created_at)
        UR->>DB: INSERT INTO user_roles (user_id, role)
        DB-->>UR: Saved User with generated ID
        UR-->>AC: User entity with audit fields
        AC-->>F: 200 OK {"message": "User registered successfully!"}
        F-->>U: Show success message + redirect to login
    end`
    },
    {
      id: 'jwt-token-validation-flow',
      title: 'JWT Token Validation (Protected Resources)',
      description: 'JWT token validation process for accessing protected API endpoints - shows the authentication filter chain.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant AF as JwtAuthenticationFilter
    participant JWT as JwtTokenProvider
    participant UP as CustomUserPrincipal
    participant UR as UserRepository
    participant C as Controller
    participant DB as Database

    Note over F,DB: Protected Resource Access
    F->>AF: GET /api/pets (or any protected endpoint)<br/>Authorization: Bearer {jwt}
    AF->>JWT: validateToken(jwt)
    JWT->>JWT: Parse JWT & verify HMAC-SHA512 signature
    JWT->>JWT: Check expiration date
    
    alt Valid Token
        JWT-->>AF: Token valid
        AF->>JWT: getUsernameFromToken(jwt)
        JWT-->>AF: Email/username from JWT claims
        AF->>UP: loadUserByUsername(email)
        UP->>UR: findByEmail(email)
        UR->>DB: SELECT * FROM users WHERE email = ?
        DB-->>UR: User entity with roles
        UR-->>UP: User details
        UP-->>AF: UserDetails with GrantedAuthorities
        AF->>AF: Set Authentication in SecurityContext
        AF->>C: Forward request with authenticated user
        C-->>AF: Protected resource data
        AF-->>F: 200 OK + Resource data
    else Invalid/Expired Token
        JWT-->>AF: Token invalid
        AF-->>F: 401 Unauthorized
    end`
    },
    {
      id: 'pets-sequence-title',
      title: 'Pets Sequence Diagrams',
      description: 'The following sequence diagrams detail the complete CRUD operations for pet management in the Pet Store API. Each diagram shows the flow from frontend request through the Spring Boot layers (Controller, Service, Repository) to the database, including authentication, authorization, and error handling patterns.',
      category: 'api-section',
      definition: ''
    },
    {
      id: 'pet-create-flow',
      title: 'Pet Creation (POST)',
      description: 'Create a new pet - Authentication required (USER or ADMIN role).',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant PC as PetController
    participant PS as PetService
    participant PR as PetRepository
    participant DB as Database

    F->>PC: POST /api/pets + JWT Auth<br/>{name, category, price, status}
    PC->>PC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    PC->>PC: Get username from SecurityContext
    PC->>PS: savePet(pet)
    PS->>PR: save(pet)
    PR->>DB: INSERT INTO pets<br/>(name, category_id, price, status, created_by...)
    DB-->>PR: Generated ID + Audit fields
    PR-->>PS: Saved Pet with ID
    PS-->>PC: Pet Response
    PC-->>F: 200 OK + Created Pet`
    },
    {
      id: 'pet-read-flow',
      title: 'Pet Retrieval (GET)',
      description: 'Retrieve pets with filtering - Public access, no authentication required.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant PC as PetController
    participant PS as PetService
    participant PR as PetRepository
    participant DB as Database

    Note over F,DB: Get All Pets with Filters
    F->>PC: GET /api/pets?name=x&categoryId=y&status=z&limit=n
    PC->>PS: findPetsByFilters(name, categoryId, status, limit)
    PS->>PR: findPetsByFilters() with @Query + Pageable
    PR->>DB: SELECT p FROM Pet p WHERE...<br/>ORDER BY created_at DESC
    DB-->>PR: Filtered Pet List
    PR-->>PS: List<Pet>
    PS-->>PC: Pet List Response
    PC-->>F: 200 OK + Pets

    Note over F,DB: Get Single Pet by ID
    F->>PC: GET /api/pets/{id}
    PC->>PS: getPetById(id)
    PS->>PR: findById(id)
    PR->>DB: SELECT * FROM pets WHERE id = ?
    DB-->>PR: Optional<Pet>
    PR-->>PS: Optional<Pet>
    PC->>PC: Check if present()
    PC-->>F: 200 OK + Pet OR 404 Not Found`
    },
    {
      id: 'pet-update-flow',
      title: 'Pet Update (PUT)',
      description: 'Update existing pet - Users can edit their own pets, Admins can edit any pet.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant PC as PetController
    participant PS as PetService
    participant PR as PetRepository
    participant CR as CategoryRepository
    participant UR as UserRepository
    participant DB as Database

    F->>PC: PUT /api/pets/{id} + JWT Auth<br/>{name, category, price, status}
    PC->>PC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    PC->>PC: Get current user from SecurityContext
    PC->>UR: findByEmail(username)
    UR->>DB: SELECT * FROM users WHERE email = ?
    DB-->>UR: User entity
    PC->>PS: getPetById(id)
    PS->>PR: findById(id)
    PR->>DB: SELECT * FROM pets WHERE id = ?
    PC->>PC: Check ownership<br/>(isAdmin OR isOwner)
    alt Permission Granted
        PC->>PS: updatePet(id, petDetails)
        PS->>CR: findById(category.id) if category provided
        PS->>PS: Update all pet fields
        PS->>PR: save(updatedPet)
        PR->>DB: UPDATE pets SET... WHERE id = ?
        PC-->>F: 200 OK + Updated Pet
    else Permission Denied
        PC-->>F: 403 Forbidden
    end`
    },
    {
      id: 'pet-delete-flow',
      title: 'Pet Deletion (DELETE)',
      description: 'Delete pet - Admin-only operation with audit logging.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant PC as PetController
    participant PS as PetService
    participant PR as PetRepository
    participant DB as Database

    F->>PC: DELETE /api/pets/{id} + JWT Auth
    PC->>PC: @PreAuthorize("hasRole('ADMIN')")
    PC->>PS: getPetById(id) for logging
    PS->>PR: findById(id)
    PR->>DB: SELECT * FROM pets WHERE id = ?
    PC->>PS: deletePet(id)
    PS->>PR: existsById(id)
    PR->>DB: SELECT COUNT(*) FROM pets WHERE id = ?
    alt Pet Exists
        PS->>PR: deleteById(id)
        PR->>DB: DELETE FROM pets WHERE id = ?
        PC-->>F: 200 OK
    else Pet Not Found
        PC-->>F: 404 Not Found
    end`
    },
    {
      id: 'users-sequence-title',
      title: 'Users Sequence Diagrams',
      description: 'The following sequence diagrams detail the complete CRUD operations for user management in the Pet Store API. Each diagram shows the flow from frontend request through the Spring Boot layers (Controller, Service, Repository) to the database, including role-based authorization, password encoding, and email validation patterns.',
      category: 'api-section',
      definition: ''
    },
    {
      id: 'user-read-flow',
      title: 'User Retrieval (GET)',
      description: 'Retrieve users with role-based access - ADMIN can access all users, USER can only access their own profile.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant UC as UserController
    participant US as UserService
    participant UR as UserRepository
    participant DB as Database

    Note over F,DB: Get All Users (ADMIN only)
    F->>UC: GET /api/users + JWT Auth
    UC->>UC: @PreAuthorize("hasRole('ADMIN')")
    UC->>US: getAllUsers()
    US->>UR: findAll()
    UR->>DB: SELECT * FROM users ORDER BY created_at
    DB-->>UR: List<User>
    UR-->>US: List<User>
    US-->>UC: User List
    UC->>UC: convertToUserResponse() - exclude passwords
    UC-->>F: 200 OK + User List (sanitized)

    Note over F,DB: Get Single User by ID
    F->>UC: GET /api/users/{id} + JWT Auth
    UC->>UC: @PreAuthorize("hasRole('ADMIN') or ownProfile")
    UC->>US: getUserById(id)
    US->>UR: findById(id)
    UR->>DB: SELECT * FROM users WHERE id = ?
    DB-->>UR: Optional<User>
    UR-->>US: Optional<User>
    UC->>UC: Check if present() & convertToUserResponse()
    UC-->>F: 200 OK + User OR 404 Not Found`
    },
    {
      id: 'user-update-flow',
      title: 'User Update (PUT)',
      description: 'Update user information - Users can edit their own profile, Admins can edit any user including roles.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant UC as UserController
    participant US as UserService
    participant UR as UserRepository
    participant PE as PasswordEncoder
    participant DB as Database

    F->>UC: PUT /api/users/{id} + JWT Auth<br/>{firstName, lastName, email, password, roles}
    UC->>UC: @PreAuthorize("hasRole('ADMIN') or ownProfile")
    UC->>UC: Get current user from SecurityContext
    UC->>UC: Check if isAdmin for role updates
    UC->>US: updateUser(id, userDetails)
    US->>UR: findById(id)
    UR->>DB: SELECT * FROM users WHERE id = ?
    
    alt Email Update Validation
        US->>UR: findByEmail(newEmail)
        UR->>DB: SELECT * FROM users WHERE email = ?
        UR-->>US: Optional<User>
        US->>US: Check email not taken by other user
    end
    
    alt Password Update
        US->>PE: encode(newPassword)
        PE-->>US: Encoded password hash
    end
    
    US->>UR: save(updatedUser)
    UR->>DB: UPDATE users SET... WHERE id = ?
    DB-->>UR: Updated User with audit fields
    UR-->>US: Saved User
    US-->>UC: Updated User
    UC->>UC: convertToUserResponse() - sanitize
    UC-->>F: 200 OK + Updated User (sanitized)`
    },
    {
      id: 'user-delete-flow',
      title: 'User Deletion (DELETE)',
      description: 'Delete user - Admin-only operation with existence validation.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant UC as UserController
    participant US as UserService
    participant UR as UserRepository
    participant DB as Database

    F->>UC: DELETE /api/users/{id} + JWT Auth
    UC->>UC: @PreAuthorize("hasRole('ADMIN')")
    UC->>US: existsById(id)
    US->>UR: existsById(id)
    UR->>DB: SELECT COUNT(*) FROM users WHERE id = ?
    
    alt User Exists
        UC->>US: deleteUser(id)
        US->>UR: findById(id) for validation
        UR->>DB: SELECT * FROM users WHERE id = ?
        US->>UR: delete(user)
        UR->>DB: DELETE FROM users WHERE id = ?
        UC-->>F: 200 OK + "User deleted successfully"
    else User Not Found
        UC-->>F: 400 Bad Request + "User not found"
    end`
    },
    {
      id: 'categories-sequence-title',
      title: 'Categories Sequence Diagrams',
      description: 'The following sequence diagrams detail the complete CRUD operations for category management in the Pet Store API. Categories provide organization for pets and require admin privileges for management operations. Each diagram shows the flow from frontend request through Spring Boot layers with proper authorization controls.',
      category: 'api-section',
      definition: ''
    },
    {
      id: 'category-create-flow',
      title: 'Category Creation (POST)',
      description: 'Create a new category - Admin-only operation with validation.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant CC as CategoryController
    participant CS as CategoryService
    participant CR as CategoryRepository
    participant DB as Database

    F->>CC: POST /api/categories + JWT Auth<br/>{name}
    CC->>CC: @PreAuthorize("hasRole('ADMIN')")
    CC->>CS: saveCategory(category)
    CS->>CR: save(category)
    CR->>DB: INSERT INTO categories<br/>(name, created_at, updated_at)
    DB-->>CR: Generated ID + Audit fields
    CR-->>CS: Saved Category with ID
    CS-->>CC: Category Response
    CC-->>F: 201 Created + Category`
    },
    {
      id: 'category-read-flow',
      title: 'Category Retrieval (GET)',
      description: 'Retrieve categories - Public access for list, Admin-only for individual category by ID.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant CC as CategoryController
    participant CS as CategoryService
    participant CR as CategoryRepository
    participant DB as Database

    Note over F,DB: Get All Categories (Public Access)
    F->>CC: GET /api/categories
    CC->>CS: getAllCategories()
    CS->>CR: findAll()
    CR->>DB: SELECT * FROM categories ORDER BY name
    DB-->>CR: List<Category>
    CR-->>CS: List<Category>
    CS-->>CC: Category List
    CC-->>F: 200 OK + Categories

    Note over F,DB: Get Single Category by ID (Admin Only)
    F->>CC: GET /api/categories/{id} + JWT Auth
    CC->>CC: @PreAuthorize("hasRole('ADMIN')")
    CC->>CS: getCategoryById(id)
    CS->>CR: findById(id)
    CR->>DB: SELECT * FROM categories WHERE id = ?
    DB-->>CR: Optional<Category>
    CR-->>CS: Optional<Category>
    CC->>CC: Check if present()
    CC-->>F: 200 OK + Category OR 404 Not Found`
    },
    {
      id: 'category-update-flow',
      title: 'Category Update (PUT)',
      description: 'Update existing category - Admin-only operation with existence validation.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant CC as CategoryController
    participant CS as CategoryService
    participant CR as CategoryRepository
    participant DB as Database

    F->>CC: PUT /api/categories/{id} + JWT Auth<br/>{name}
    CC->>CC: @PreAuthorize("hasRole('ADMIN')")
    CC->>CS: updateCategory(id, categoryDetails)
    CS->>CR: findById(id)
    CR->>DB: SELECT * FROM categories WHERE id = ?
    DB-->>CR: Optional<Category>
    
    alt Category Exists
        CS->>CS: Update category.name
        CS->>CR: save(updatedCategory)
        CR->>DB: UPDATE categories SET name = ?, updated_at = ? WHERE id = ?
        DB-->>CR: Updated Category with audit fields
        CR-->>CS: Saved Category
        CS-->>CC: Updated Category
        CC-->>F: 200 OK + Updated Category
    else Category Not Found
        CS-->>CC: null
        CC-->>F: 404 Not Found
    end`
    },
    {
      id: 'category-delete-flow',
      title: 'Category Deletion (DELETE)',
      description: 'Delete category - Admin-only operation with existence check.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant CC as CategoryController
    participant CS as CategoryService
    participant CR as CategoryRepository
    participant DB as Database

    F->>CC: DELETE /api/categories/{id} + JWT Auth
    CC->>CC: @PreAuthorize("hasRole('ADMIN')")
    CC->>CS: deleteCategory(id)
    CS->>CR: existsById(id)
    CR->>DB: SELECT COUNT(*) FROM categories WHERE id = ?
    
    alt Category Exists
        CS->>CR: deleteById(id)
        CR->>DB: DELETE FROM categories WHERE id = ?
        CS-->>CC: true (deleted successfully)
        CC-->>F: 204 No Content
    else Category Not Found
        CS-->>CC: false (not found)
        CC-->>F: 404 Not Found
    end`
    },
    {
      id: 'user-journey',
      title: 'User Journey Flow',
      description: 'Complete user experience flow from landing page to pet purchase, including authentication and search.',
      category: 'user-flow',
      definition: `flowchart TD
    A[🏠 Landing Page] --> B{User Logged In?}
    B -->|No| C[🔐 Login Page]
    B -->|Yes| D[📋 Pet List View]
    
    C --> C1[Enter Credentials]
    C1 --> C2{Valid Login?}
    C2 -->|No| C3[❌ Show Error]
    C3 --> C
    C2 -->|Yes| D
    
    D --> D1[🔍 Search/Filter Pets]
    D1 --> D2[📱 View Pet Cards]
    D2 --> E[👁️ Pet Detail View]
    
    E --> F{Pet Available?}
    F -->|No| G[😞 Show Unavailable]
    F -->|Yes| H[🛒 Purchase Pet]
    G --> D1

    H --> I[✅ Submit Purchase]
    I --> J[🎉 Success Message]
    
    D --> L[➕ Add New Pet]
    L --> M[📝 Pet Form]
    M --> N[📤 Submit Pet Data]
    N --> O{Validation OK?}
    O -->|No| P[❌ Show Errors]
    P --> M
    O -->|Yes| Q[✅ Pet Added]
    Q --> D

    classDef startEnd fill:#e1f5fe
    classDef process fill:#f3e5f5
    classDef decision fill:#fff3e0
    classDef success fill:#e8f5e8
    classDef error fill:#ffebee

    class A,K,Q startEnd
    class C1,D1,D2,I,M,N process
    class B,C2,F,O decision
    class K,Q success
    class C3,G,P error`
    },
    {
      id: 'docker-deployment',
      title: 'Docker Deployment Architecture',
      description: 'Actual Docker Compose setup with multi-stage builds, health checks, and container networking based on real configuration files.',
      category: 'deployment',
      definition: `graph TB
    subgraph "External Layer"
        USER[👤 Users<br/>Web Browser]
        WEB[🌐 Internet<br/>HTTP/HTTPS]
    end

    subgraph "Docker Network: petstore-network"
        
        subgraph "Frontend Container"
            NGINX[🌐 petstore-frontend<br/>nginx:alpine<br/>Container Port: 80<br/>Host Port: 80]
            
            STATIC[📁 Angular Static Files<br/>/usr/share/nginx/html/browser<br/>Production Build Artifacts]
            
            PROXY[⚙️ nginx.conf Configuration<br/>Route: / → Static Files<br/>Route: /api → Backend Proxy]
        end

        subgraph "Backend Container"  
            SPRING[☕ petstore-backend<br/>eclipse-temurin:17-jdk<br/>Container Port: 8080<br/>Host Port: 8080]
            
            JAR[📦 Application JAR<br/>pet-store-api-1.0.0.jar<br/>Spring Boot Executable]
            
            CONFIG[🔧 Docker Configuration<br/>application-docker.properties<br/>JWT Secret and Database URL]
        end

        subgraph "Database Container"
            MYSQL[🗄️ petstore-mysql<br/>mysql:8.0<br/>Container Port: 3306<br/>Host Port: 3306]
            
            DATABASE[💾 MySQL Database<br/>petstore_db<br/>Credentials: root/xxxxxxx]
            
            VOLUME[📀 Persistent Storage<br/>Volume: mysql_data<br/>Mount: /var/lib/mysql]
            
            HEALTH[❤️ Health Monitoring<br/>Check: mysqladmin ping<br/>Retry: 10 times, 5s interval]
        end
    end

    %% User Flow
    USER --> WEB
    WEB -->|HTTP Port 80| NGINX
    
    %% Frontend Internal
    NGINX --> STATIC
    NGINX --> PROXY
    
    %% API Routing
    PROXY -->|Proxy /api requests| SPRING
    WEB -.->|Direct API Access<br/>Port 8080| SPRING
    
    %% Backend Internal  
    SPRING --> JAR
    SPRING --> CONFIG
    
    %% Database Connection
    CONFIG -->|JDBC Connection<br/>petstore-mysql:3306| MYSQL
    SPRING -.->|Depends On| HEALTH
    
    %% Database Internal
    MYSQL --> DATABASE
    MYSQL --> VOLUME
    MYSQL --> HEALTH

    %% Styling
    classDef userLayer fill:#f3e5f5,stroke:#7b1fa2,stroke-width:3px,color:#000
    classDef frontend fill:#e3f2fd,stroke:#1976d2,stroke-width:3px,color:#000  
    classDef backend fill:#fff3e0,stroke:#f57c00,stroke-width:3px,color:#000
    classDef database fill:#e8f5e8,stroke:#388e3c,stroke-width:3px,color:#000
    
    class USER,WEB userLayer
    class NGINX,STATIC,PROXY frontend
    class SPRING,JAR,CONFIG backend
    class MYSQL,DATABASE,VOLUME,HEALTH database`
    }
  ];

  getDiagramsByCategory(category: DiagramData['category']): DiagramData[] {
    if (category === 'api') {
      return this.diagrams.filter(diagram => 
        diagram.category === 'api' || diagram.category === 'api-section'
      );
    }
    return this.diagrams.filter(diagram => diagram.category === category);
  }
}