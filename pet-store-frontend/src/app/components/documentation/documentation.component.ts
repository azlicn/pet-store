import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MermaidDiagramComponent } from '../mermaid-diagram/mermaid-diagram.component';
import { DesignPatternDocComponent } from "../docs/design-pattern-doc/design-pattern-doc.component";

interface DiagramData {
  id: string;
  title: string;
  description: string;
  definition: string;
  category: 'architecture' | 'database' | 'api' | 'api-section' | 'user-flow' | 'deployment' | 'design-patterns';
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
    MermaidDiagramComponent,
    DesignPatternDocComponent
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
      id: 'component-architecture-frontend',
      title: 'Layer 1: Frontend Components',
      description: 'Angular components structure showing routing, user interface components, and their relationships.',
      category: 'architecture',
      definition: `graph TD
    subgraph "Public Routes"
        HC[🏠 HomeComponent<br/>Landing Page]
        LC[🔐 LoginComponent<br/>User Auth]
        RC[📝 RegisterComponent<br/>New User Registration]
        UC[� UnauthorizedComponent<br/>Access Denied]
        DC[📖 DocumentationComponent<br/>System Docs]
    end

    subgraph "Pet Management - Protected"
        PLC[� PetListComponent<br/>Browse Pets]
        PFC[➕ PetFormComponent<br/>Add/Edit Pet<br/>Guard: authGuard/petOwnershipGuard]
    end

    subgraph "Admin Only - Categories"
        CLC[🏷️ CategoryListComponent<br/>Category Management<br/>Guard: adminGuard]
        CFC[📝 CategoryFormComponent<br/>Add/Edit Category<br/>Guard: adminGuard]
    end

    subgraph "Admin Only - Discounts"
        DLC[🎁 DiscountListComponent<br/>Discount Management<br/>Guard: adminGuard]
        DFC[📝 DiscountFormComponent<br/>Add/Edit Discount<br/>Guard: adminGuard]
    end

    subgraph "Admin Only - Users"
        ULC[👥 UserListComponent<br/>User Management<br/>Guard: adminGuard]
        UEC[✏️ UserEditComponent<br/>Edit User Profile<br/>Guard: userProfileGuard]
    end

    subgraph "Shopping & Orders - Protected"
        CC[🛒 CartComponent<br/>Shopping Cart]
        COV[🛒 CartOverlayComponent<br/>Quick Cart View]
        CHC[💳 CheckoutComponent<br/>Order Checkout<br/>Guard: authGuard/checkoutStatusGuard]
        OLC[📦 OrderListComponent<br/>My Orders<br/>Guard: authGuard]
        OHC[📋 OrderHistoryComponent<br/>Order Details<br/>Guard: orderOwnershipGuard]
        OCC[🎴 OrderCardComponent<br/>Order Summary Card]
    end

    subgraph "Address Management - Protected"
        ABC[📍 AddressBookComponent<br/>Manage Addresses]
        ADC[📝 AddressComponent<br/>Add/Edit Address]
    end

    subgraph "Shared Components"
        HC2[🎯 HeaderComponent<br/>Navigation Bar]
        PCC[🎴 PetCardComponent<br/>Reusable Pet Card]
        PLVC[📱 PetListViewComponent<br/>List/Grid Toggle]
        IMC[🖼️ ImageModalComponent<br/>Image Viewer]
        LPC[⭐ LatestPetCardComponent<br/>Featured Pet Display]
        CDC[❓ ConfirmDialogComponent<br/>Confirmation Modal]
        UDSD[🚚 UpdateDeliveryStatusDialog<br/>Delivery Update]
        PPD[⏳ PaymentProcessingDialog<br/>Payment Status]
        DVC[📊 DiagramViewerComponent<br/>Mermaid Viewer]
        MDC[📈 MermaidDiagramComponent<br/>Diagram Renderer]
    end

    HC --> LPC
    HC --> PLC
    LC --> HC2
    RC --> HC2
    
    PLC --> PCC
    PLC --> PLVC
    PLC --> IMC
    PLC --> PFC
    
    CLC --> CFC
    DLC --> DFC
    ULC --> UEC
    
    CC --> COV
    CC --> CHC
    CHC --> ABC
    CHC --> ADC
    CHC --> OLC
    OLC --> OHC
    OLC --> OCC
    OHC --> UDSD
    CHC --> PPD
    
    DC --> DVC
    DVC --> MDC
    
    HC2 --> CDC

    classDef public fill:#e3f2fd,stroke:#1976d2,stroke-width:3px,color:#000
    classDef protected fill:#fff3e0,stroke:#f57c00,stroke-width:2px,color:#000
    classDef admin fill:#ffcdd2,stroke:#d32f2f,stroke-width:3px,color:#000
    classDef shared fill:#e8f5e9,stroke:#388e3c,stroke-width:2px,color:#000

    class HC,LC,RC,UC,DC public
    class PLC,PFC,CC,COV,CHC,OLC,OHC,ABC,ADC protected
    class CLC,CFC,DLC,DFC,ULC,UEC admin
    class HC2,PCC,PLVC,IMC,LPC,OCC,CDC,UDSD,PPD,DVC,MDC shared`
    },
    {
      id: 'component-architecture-backend',
      title: 'Layer 2: Backend Services',
      description: 'Spring Boot backend architecture showing controllers, services, repositories, and their dependencies.',
      category: 'architecture',
      definition: `graph TD
    subgraph "Controllers Layer"
        AC[🔐 AuthController<br/>Authentication]
        PC[🐾 PetController<br/>Pet Management]
        CC[🏷️ CategoryController<br/>Category Management]
        UC[👥 UserController<br/>User Management]
        CTC[🛒 CartController<br/>Cart Operations]
        OC[📦 OrderController<br/>Order Management]
        ADC[📍 AddressController<br/>Address Management]
        DC[🎁 DiscountController<br/>Discount Management]
    end

    subgraph "Services Layer"
        AS[🔑 AuthService<br/>JWT & Auth Logic]
        PS[🐾 PetService<br/>Business Logic]
        CS[🏷️ CategoryService<br/>Business Logic]
        US[👥 UserService<br/>Business Logic]
        CTS[🛒 CartService<br/>Cart Operations]
        OS[📦 OrderService<br/>Order Processing]
        ADS[📍 AddressService<br/>Address Management]
        DS[🎁 DiscountService<br/>Discount Logic]
        DELS[🚚 DeliveryService<br/>Delivery Management]
        PYS[💳 PaymentService<br/>Payment Processing]
    end

    subgraph "Repositories Layer"
        UR[👥 UserRepository<br/>JPA Interface]
        PR[🐾 PetRepository<br/>JPA Interface]
        CR[🏷️ CategoryRepository<br/>JPA Interface]
        CTR[🛒 CartRepository<br/>JPA Interface]
        CIR[📦 CartItemRepository<br/>JPA Interface]
        OR[📦 OrderRepository<br/>JPA Interface]
        OIR[📦 OrderItemRepository<br/>JPA Interface]
        ADR[📍 AddressRepository<br/>JPA Interface]
        DR[🎁 DiscountRepository<br/>JPA Interface]
        DELR[🚚 DeliveryRepository<br/>JPA Interface]
        PYR[💳 PaymentRepository<br/>JPA Interface]
    end

    AC --> AS
    PC --> PS
    CC --> CS
    UC --> US
    CTC --> CTS
    OC --> OS
    ADC --> ADS
    DC --> DS
    
    AS --> UR
    PS --> PR
    PS --> CR
    CS --> CR
    US --> UR
    CTS --> CTR
    CTS --> CIR
    OS --> OR
    OS --> OIR
    OS --> DELS
    OS --> PYS
    ADS --> ADR
    DS --> DR
    DELS --> DELR
    PYS --> PYR

    classDef controller fill:#ffccbc,stroke:#f57c00,stroke-width:3px,color:#000
    classDef service fill:#c8e6c9,stroke:#388e3c,stroke-width:3px,color:#000
    classDef repository fill:#e1bee7,stroke:#7b1fa2,stroke-width:3px,color:#000

    class AC,PC,CC,UC,CTC,OC,ADC,DC controller
    class AS,PS,CS,US,CTS,OS,ADS,DS,DELS,PYS service
    class UR,PR,CR,CTR,CIR,OR,OIR,ADR,DR,DELR,PYR repository`
    },
    {
      id: 'component-architecture-database',
      title: 'Layer 3: Database Entities',
      description: 'Database entities and their relationships showing the data model layer.',
      category: 'architecture',
      definition: `graph TD
    subgraph "Core Entities"
        PE[🐾 Pet Entity<br/>Pet Information]
        CE[🏷️ Category Entity<br/>Pet Categories]
        UE[👤 User Entity<br/>User Accounts]
        RE[🔐 Role Entity<br/>User Roles]
    end

    subgraph "Store Entities"
        CAE[🛒 Cart Entity<br/>Shopping Cart]
        CIE[📦 Cart Item Entity<br/>Cart Items]
        OE[📦 Order Entity<br/>Order Information]
        OIE[📦 Order Item Entity<br/>Order Items]
    end

    subgraph "Transaction Entities"
        PAE[💳 Payment Entity<br/>Payment Records]
        DE[🚚 Delivery Entity<br/>Delivery Info]
        AE[📍 Address Entity<br/>User Addresses]
        DCE[🎁 Discount Entity<br/>Discount Codes]
    end

    subgraph "Audit Entities"
        ALE[📋 Audit Log Entity<br/>System Audit Trail]
    end

    PE -->|ManyToOne| CE
    PE -->|ManyToOne| UE
    
    UE -->|ManyToMany| RE
    UE -->|OneToMany| AE
    UE -->|OneToOne| CAE
    UE -->|OneToMany| OE
    
    CAE -->|OneToMany| CIE
    CIE -->|ManyToOne| PE
    
    OE -->|OneToMany| OIE
    OE -->|ManyToOne| AE
    OE -->|OneToOne| PAE
    OE -->|OneToOne| DE
    OE -->|ManyToOne| DCE
    
    OIE -->|ManyToOne| PE

    classDef core fill:#e3f2fd,stroke:#1976d2,stroke-width:3px,color:#000
    classDef store fill:#fff3e0,stroke:#f57c00,stroke-width:3px,color:#000
    classDef transaction fill:#e8f5e8,stroke:#388e3c,stroke-width:3px,color:#000
    classDef audit fill:#f3e5f5,stroke:#7b1fa2,stroke-width:3px,color:#000

    class PE,CE,UE,RE core
    class CAE,CIE,OE,OIE store
    class PAE,DE,AE,DCE transaction
    class ALE audit`
    },
    {
      id: 'database-schema',
      title: 'Database Entity Relationships',
      description: 'Complete Entity Relationship Diagram showing the full database schema with all tables, relationships, and key constraints including pets, users, shopping cart, orders, payments, delivery, addresses, discounts, and audit logs with comprehensive test coverage.',
      category: 'database',
      definition: `erDiagram
    %% Core Entities
    pets {
        bigint id PK
        string name "NOT NULL, MAX 50 chars"
        string description "MAX 200 chars"
        decimal price "NOT NULL, POSITIVE, PRECISION(10,2)"
        string status "ENUM: AVAILABLE, PENDING, SOLD - DEFAULT AVAILABLE"
        bigint category_id FK "NOT NULL, EAGER"
        bigint owner_id FK "NULLABLE, LAZY - Pet Owner"
        datetime created_at "Audit: @CreatedDate, immutable"
        datetime updated_at "Audit: @LastModifiedDate"
        bigint created_by "Audit: @CreatedBy, immutable"
        bigint last_modified_by "Audit: @LastModifiedBy"
    }
    
    categories {
        bigint id PK
        string name "NOT NULL, UNIQUE, MAX 30 chars"
        datetime created_at "@CreatedDate"
        datetime updated_at "@LastModifiedDate"
    }
    
    users {
        bigint id PK
        string email "NOT NULL, UNIQUE, MAX 150 chars"
        string password "NOT NULL, MIN 6-100 chars, BCrypt encoded"
        string first_name "NOT NULL, MAX 100 chars"
        string last_name "NOT NULL, MAX 100 chars"
        string phone_number "MAX 20 chars"
        datetime created_at "@CreatedDate"
        datetime updated_at "@LastModifiedDate"
    }
    
    %% Pet Related Collections
    pet_photos {
        bigint pet_id FK
        string photo_url "ElementCollection - Photo URLs"
    }
    
    pet_tags {
        bigint pet_id FK
        string tag "ElementCollection - Searchable tags"
    }
    
    %% User Roles (ManyToMany via Join Table)
    user_roles {
        bigint user_id FK
        string role "ENUM: ROLE_USER, ROLE_ADMIN"
    }
    
    %% Shopping Cart
    carts {
        bigint id PK
        bigint user_id FK "UNIQUE, NOT NULL - OneToOne with User"
    }
    
    cart_items {
        bigint id PK
        bigint cart_id FK "NOT NULL"
        bigint pet_id FK "NOT NULL, UNIQUE per cart"
        decimal price "NOT NULL, PRECISION(10,2) - Snapshot"
    }
    
    %% Orders
    orders {
        bigint id PK
        string order_number "NOT NULL, UNIQUE - Generated"
        bigint user_id FK "NOT NULL"
        string status "ENUM: PLACED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED"
        decimal total_amount "NOT NULL, PRECISION(10,2)"
        bigint discount_id FK "NULLABLE - Reference"
        string discount_code "Snapshot: Applied discount code"
        decimal discount_percentage "Snapshot: PRECISION(5,2)"
        decimal discount_amount "Snapshot: PRECISION(10,2)"
        bigint shipping_address_id FK "NULLABLE"
        bigint billing_address_id FK "NULLABLE"
        datetime created_at "@CreatedDate"
        datetime updated_at "@LastModifiedDate"
    }
    
    order_items {
        bigint id PK
        bigint order_id FK "NOT NULL"
        bigint pet_id FK "NOT NULL"
        decimal price "NOT NULL, PRECISION(10,2) - Snapshot"
    }
    
    %% Payment
    payments {
        bigint id PK
        bigint order_id FK "NOT NULL, OneToOne"
        decimal amount "NOT NULL, PRECISION(10,2)"
        string status "ENUM: PENDING, COMPLETED, FAILED, REFUNDED"
        string payment_type "ENUM: CREDIT_CARD, DEBIT_CARD, E_WALLET, PAYPAL, CASH_ON_DELIVERY"
        string e_wallet_type "ENUM: GRABPAY, TOUCHNGO, BOOST - For E_WALLET"
        string payment_note "Optional payment details"
        datetime paid_at "Timestamp of successful payment"
    }
    
    %% Delivery (100% Test Coverage)
    deliveries {
        bigint id PK
        bigint order_id FK "NOT NULL, OneToOne"
        string name "NOT NULL - Recipient name"
        string phone "NOT NULL - Contact number"
        string address "NOT NULL - Delivery address"
        string status "ENUM: PENDING, SHIPPED, DELIVERED - DEFAULT PENDING"
        datetime created_at "Order creation time"
        datetime shipped_at "Shipment timestamp"
        datetime delivered_at "Delivery completion timestamp"
    }
    
    %% Addresses
    addresses {
        bigint id PK
        bigint user_id FK "NOT NULL"
        string full_name "NOT NULL"
        string phone_number "NOT NULL, MAX 20 chars"
        string street "NOT NULL, MAX 255 chars"
        string city "NOT NULL, MAX 100 chars"
        string state "NOT NULL, MAX 100 chars"
        string postal_code "NOT NULL, MAX 20 chars"
        string country "NOT NULL, MAX 100 chars"
        boolean is_default "DEFAULT false"
    }
    
    %% Discounts (95% Test Coverage)
    discounts {
        bigint id PK
        string code "UNIQUE, NOT NULL, MAX 20 chars"
        decimal percentage "NOT NULL, PRECISION(10,2)"
        datetime valid_from "NOT NULL - Start date"
        datetime valid_to "NOT NULL - End date"
        string description "NULLABLE, MAX 200 chars"
        boolean active "NOT NULL, DEFAULT true"
        datetime created_at "@PrePersist"
        datetime updated_at "@PreUpdate"
    }
    
    %% Audit Logs (93% Test Coverage)
    audit_logs {
        bigint id PK
        string entity_type "NOT NULL, @NotBlank - Entity class name"
        bigint entity_id "NOT NULL, @NotNull - Affected entity ID"
        bigint user_id FK "NULLABLE, LAZY - User who performed action"
        string action "Action type: CREATE, UPDATE, DELETE"
        text old_value "Previous state JSON"
        text new_value "New state JSON"
        datetime created_at "@CreatedDate, immutable"
        datetime updated_at "@LastModifiedDate"
        bigint created_by "@CreatedBy, immutable"
        bigint last_modified_by "@LastModifiedBy"
    }

    %% Core Relationships
    categories ||--o{ pets : "has many"
    users ||--o{ pets : "owns (nullable)"
    pets ||--o{ pet_photos : "has many photos"
    pets ||--o{ pet_tags : "has many tags"
    users ||--o{ user_roles : "has roles"
    
    %% Shopping Cart Relationships
    users ||--|| carts : "has one cart"
    carts ||--o{ cart_items : "contains items"
    cart_items }o--|| pets : "references pet"
    
    %% Order Relationships
    users ||--o{ orders : "places orders"
    orders ||--o{ order_items : "contains items"
    order_items }o--|| pets : "purchases pet"
    orders }o--o| addresses : "ships to"
    orders }o--o| addresses : "bills to"
    orders }o--o| discounts : "applies discount"
    
    %% Payment & Delivery Relationships
    orders ||--o| payments : "paid by (OneToOne)"
    orders ||--o| deliveries : "delivered by (OneToOne)"
    
    %% Address Relationships
    users ||--o{ addresses : "has many addresses"
    
    %% Audit Relationships
    users ||--o{ audit_logs : "actions logged"
    audit_logs }o--|| users : "performed by (nullable)"`
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
      description: 'The following sequence diagrams detail the complete CRUD operations for pet management in the Pet Store API. Each diagram shows the flow from frontend request through the Spring Boot layers (Controller, Service, Repository) to the database, including authentication, authorization, pagination, filtering, and comprehensive error handling patterns.',
      category: 'api-section',
      definition: ''
    },
    {
      id: 'pet-create-flow',
      title: 'Pet Creation (POST /api/pets)',
      description: 'Create a new pet - Authentication required (USER or ADMIN role). Returns 201 Created with Location header.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant PC as PetController
    participant PS as PetService
    participant PR as PetRepository
    participant DB as Database

    F->>PC: POST /api/pets + JWT Auth<br/>{name, description, category, price, status, photoUrls, tags}
    PC->>PC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    PC->>PC: @Valid validation on @RequestBody
    
    alt Validation Fails
        PC-->>F: 400 Bad Request<br/>Validation errors
    else Validation Passes
        PC->>PS: savePet(pet)
        PS->>PS: Validate pet is not null
        PS->>PR: save(pet)
        PR->>DB: INSERT INTO pets<br/>(name, description, category_id, price, status)<br/>@CreatedBy, @CreatedDate auto-populated
        DB-->>PR: Generated ID + Audit fields (created_by, created_at)
        PR-->>PS: Saved Pet with ID
        PS-->>PC: Pet Response
        PC->>PC: Build Location URI: /api/pets/{id}
        PC-->>F: 201 Created + Location header<br/>Body: Pet with ID
    end`
    },
    {
      id: 'pet-read-all-flow',
      title: 'Pet Retrieval - List All (GET /api/pets)',
      description: 'Retrieve pets with pagination and filtering - Public access, no authentication required. Returns paginated response.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant PC as PetController
    participant PS as PetService
    participant PR as PetRepository
    participant DB as Database

    F->>PC: GET /api/pets?name=x&categoryId=y&status=z<br/>&page=0&size=10
    PC->>PC: Extract query parameters<br/>(name, categoryId, status, page, size)
    PC->>PS: findPetsByFiltersPaginated(name, categoryId, status, null, page, size)
    PS->>PS: Create Pageable with PageRequest.of(page, size)
    PS->>PR: findPetsByFiltersPaginated(name, categoryId, status, userId, pageable)
    PR->>DB: SELECT p FROM Pet p<br/>WHERE name LIKE ? AND category_id = ? AND status = ?<br/>ORDER BY created_at DESC<br/>LIMIT size OFFSET (page * size)
    DB-->>PR: Page<Pet> with filtered results
    PR-->>PS: Page<Pet>
    PS-->>PC: Page<Pet>
    PC->>PC: Create PetPageResponse with<br/>(content, pageNumber, pageSize, totalElements, totalPages)
    PC-->>F: 200 OK<br/>PetPageResponse with pagination metadata`
    },
    {
      id: 'pet-read-single-flow',
      title: 'Pet Retrieval - By ID (GET /api/pets/{id})',
      description: 'Retrieve a single pet by ID - Public access. Returns pet details or 404 if not found.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant PC as PetController
    participant PS as PetService
    participant PR as PetRepository
    participant DB as Database

    F->>PC: GET /api/pets/{id}
    PC->>PS: getPetById(id)
    
    alt ID is null
        PS-->>PC: throw InvalidPetException
        PC-->>F: 400 Bad Request
    else ID provided
        PS->>PR: findById(id)
        PR->>DB: SELECT * FROM pets WHERE id = ?
        
        alt Pet Found
            DB-->>PR: Pet entity with category, audit fields
            PR-->>PS: Optional<Pet> present
            PS-->>PC: Pet
            PC-->>F: 200 OK + Pet details
        else Pet Not Found
            DB-->>PR: Empty result
            PR-->>PS: Optional<Pet> empty
            PS-->>PC: throw PetNotFoundException(id)
            PC-->>F: 404 Not Found<br/>"Pet not found with ID '{id}'"
        end
    end`
    },
    {
      id: 'pet-read-latest-flow',
      title: 'Pet Retrieval - Latest Available (GET /api/pets/latest)',
      description: 'Retrieve latest available pets for homepage - Public access. Returns recent AVAILABLE pets sorted by creation date.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant PC as PetController
    participant PS as PetService
    participant PR as PetRepository
    participant DB as Database

    F->>PC: GET /api/pets/latest?limit=6
    PC->>PS: getLatestAvailablePets(limit)
    PS->>PS: Create Pageable(0, limit)
    PS->>PR: findLatestPetsByStatus(PetStatus.AVAILABLE, pageable)
    PR->>DB: SELECT * FROM pets<br/>WHERE status = 'AVAILABLE'<br/>ORDER BY created_at DESC<br/>LIMIT limit
    DB-->>PR: List<Pet> latest available pets
    PR-->>PS: List<Pet>
    PS-->>PC: List<Pet>
    PC-->>F: 200 OK + List of latest pets`
    },
    {
      id: 'pet-read-mypets-flow',
      title: 'Pet Retrieval - My Pets (GET /api/pets/my-pets)',
      description: 'Retrieve pets created by current user - Authentication required (USER or ADMIN role). Returns paginated user pets.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant PC as PetController
    participant PS as PetService
    participant US as UserService
    participant UR as UserRepository
    participant PR as PetRepository
    participant DB as Database

    F->>PC: GET /api/pets/my-pets?page=0&size=10 + JWT Auth
    PC->>PC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    PC->>PC: Get email from SecurityContext.getAuthentication()
    PC->>US: getUserByEmail(userEmail)
    US->>UR: findByEmail(userEmail)
    UR->>DB: SELECT * FROM users WHERE email = ?
    
    alt User Found
        DB-->>UR: User entity with ID
        UR-->>US: Optional<User> present
        US-->>PC: User
        PC->>PS: findPetsByFiltersPaginated(name, categoryId, status, user.getId(), page, size)
        PS->>PR: findPetsByFiltersPaginated(..., userId, pageable)
        PR->>DB: SELECT p FROM Pet p<br/>WHERE created_by = userId<br/>AND (filters...)<br/>ORDER BY created_at DESC
        DB-->>PR: Page<Pet> user's pets
        PR-->>PS: Page<Pet>
        PS-->>PC: Page<Pet>
        PC->>PC: Create PetPageResponse
        PC-->>F: 200 OK + User's pets paginated
    else User Not Found
        US-->>PC: throw ResponseStatusException(UNAUTHORIZED)
        PC-->>F: 401 Unauthorized "User not found"
    end`
    },
    {
      id: 'pet-update-flow',
      title: 'Pet Update (PUT /api/pets/{id})',
      description: 'Update existing pet - Users can edit their own pets (created_by), Admins can edit any pet. Validates ownership and category.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant PC as PetController
    participant PS as PetService
    participant US as UserService
    participant UR as UserRepository
    participant PR as PetRepository
    participant CR as CategoryRepository
    participant DB as Database

    F->>PC: PUT /api/pets/{id} + JWT Auth<br/>{name, description, category, price, status, photoUrls, tags}
    PC->>PC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    PC->>PC: @Valid validation on @RequestBody
    PC->>PC: Get username from SecurityContext
    PC->>US: getUserByEmail(username)
    US->>UR: findByEmail(username)
    UR->>DB: SELECT * FROM users WHERE email = ?
    DB-->>UR: User entity with roles
    UR-->>US: Optional<User>
    US-->>PC: User (currentUser)
    
    PC->>PS: getPetById(id)
    PS->>PR: findById(id)
    PR->>DB: SELECT * FROM pets WHERE id = ?
    
    alt Pet Found
        DB-->>PR: Pet entity with created_by
        PR-->>PS: Pet (existingPet)
        PS-->>PC: Pet
        
        PC->>PC: Check currentUser.roles.contains(Role.ADMIN)
        PC->>PC: Check existingPet.createdBy == currentUser.id
        
        alt Admin OR Owner
            PC->>PS: updatePet(id, petDetails)
            PS->>PR: findById(id)
            PS->>PS: Update fields: name, description, price, status, photoUrls, tags
            
            alt Category Provided
                PS->>CR: findById(category.id)
                CR->>DB: SELECT * FROM categories WHERE id = ?
                
                alt Category Found
                    DB-->>CR: Category entity
                    CR-->>PS: Category
                    PS->>PS: existingPet.setCategory(category)
                else Category Not Found
                    CR-->>PS: throw CategoryNotFoundException
                    PS-->>PC: CategoryNotFoundException
                    PC-->>F: 404 Not Found "Category not found"
                end
            end
            
            PS->>PR: save(updatedPet)
            PR->>DB: UPDATE pets SET name=?, description=?, price=?,<br/>status=?, category_id=?, updated_at=NOW()<br/>WHERE id = ?
            DB-->>PR: Updated Pet with @LastModifiedDate
            PR-->>PS: Updated Pet
            PS-->>PC: Updated Pet
            PC-->>F: 200 OK + Updated Pet
        else Not Admin AND Not Owner
            PC-->>F: 403 Forbidden<br/>"You are not allowed to update this pet"
        end
    else Pet Not Found
        PS-->>PC: throw PetNotFoundException
        PC-->>F: 404 Not Found
    end`
    },
    {
      id: 'pet-delete-flow',
      title: 'Pet Deletion (DELETE /api/pets/{id})',
      description: 'Delete pet - Admin-only operation. Returns 204 No Content on success.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant PC as PetController
    participant PS as PetService
    participant PR as PetRepository
    participant DB as Database

    F->>PC: DELETE /api/pets/{id} + JWT Auth
    PC->>PC: @PreAuthorize("hasRole('ADMIN')")
    
    PC->>PS: deletePet(id)
    
    alt ID is null
        PS-->>PC: throw InvalidPetException
        PC-->>F: 400 Bad Request
    else ID provided
        PS->>PR: findById(id)
        PR->>DB: SELECT * FROM pets WHERE id = ?
        
        alt Pet Found
            DB-->>PR: Pet entity
            PR-->>PS: Pet
            PS->>PR: delete(pet)
            PR->>DB: DELETE FROM pets WHERE id = ?<br/>CASCADE delete pet_photos, pet_tags
            DB-->>PR: Success
            PR-->>PS: void
            PS-->>PC: void
            PC-->>F: 204 No Content
        else Pet Not Found
            PR-->>PS: throw PetNotFoundException
            PS-->>PC: PetNotFoundException
            PC-->>F: 404 Not Found<br/>"Pet not found with ID '{id}'"
        end
    end`
    },
    {
      id: 'address-sequence-title',
      title: 'Address Sequence Diagrams',
      description: 'The following sequence diagrams detail address management operations for authenticated users in the Pet Store API. Each diagram shows the flow from frontend request through the controller, service, and repository layers, including user authentication and validation.',
      category: 'api-section',
      definition: ''
    },
    {
      id: 'address-list-flow',
      title: 'Address List (GET /api/users/addresses)',
      description: 'Retrieve all addresses for authenticated user - Authentication required (USER or ADMIN role).',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant AC as AddressController
    participant US as UserService
    participant AS as AddressService
    participant AR as AddressRepository
    participant DB as Database

    F->>AC: GET /api/users/addresses + JWT Auth
    AC->>AC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    AC->>AC: Get email from SecurityContext.getAuthentication()
    AC->>US: getUserByEmail(userEmail)
    US->>DB: SELECT * FROM users WHERE email = ?
    
    alt User Found
        DB-->>US: User entity with ID
        US-->>AC: Optional<User> present
        AC->>AS: getUserAddresses(user.getId())
        AS->>AR: findByUserId(userId)
        AR->>DB: SELECT * FROM addresses WHERE user_id = ?<br/>ORDER BY created_at DESC
        DB-->>AR: List<Address>
        AR-->>AS: List<Address>
        AS-->>AC: List<Address>
        AC-->>F: 200 OK + List of addresses
    else User Not Found
        US-->>AC: Optional<User> empty
        AC-->>F: 400 Bad Request
    end`
    },
    {
      id: 'address-create-flow',
      title: 'Address Creation (POST /api/users/addresses)',
      description: 'Create new address for authenticated user - Returns 201 Created with address details.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant AC as AddressController
    participant US as UserService
    participant AS as AddressService
    participant AR as AddressRepository
    participant DB as Database

    F->>AC: POST /api/users/addresses + JWT Auth<br/>{fullName, phoneNumber, street, city, state, postalCode, country}
    AC->>AC: @PreAuthorize("hasRole('USER')")
    AC->>AC: @Valid validation on @RequestBody
    
    alt Validation Fails
        AC-->>F: 400 Bad Request<br/>Validation errors (empty street, etc.)
    else Validation Passes
        AC->>AC: Get email from SecurityContext
        AC->>US: getUserByEmail(userEmail)
        US->>DB: SELECT * FROM users WHERE email = ?
        
        alt User Found
            DB-->>US: User entity
            US-->>AC: Optional<User> present
            AC->>AS: createAddress(user.getId(), address)
            AS->>AS: Set user relationship
            AS->>AR: save(address)
            AR->>DB: INSERT INTO addresses<br/>(user_id, full_name, phone_number, street, city, state, postal_code, country)
            DB-->>AR: Saved Address with generated ID
            AR-->>AS: Address
            AS-->>AC: Address
            AC-->>F: 201 Created + Address
        else User Not Found
            US-->>AC: Optional<User> empty
            AC-->>F: 400 Bad Request
        end
    end`
    },
    {
      id: 'address-update-flow',
      title: 'Address Update (PUT /api/users/addresses/{addressId})',
      description: 'Update existing address - USER can update their own addresses.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant AC as AddressController
    participant AS as AddressService
    participant AR as AddressRepository
    participant DB as Database

    F->>AC: PUT /api/users/addresses/{addressId} + JWT Auth<br/>{fullName, phoneNumber, street, city, state, postalCode, country}
    AC->>AC: @PreAuthorize("hasRole('USER')")
    AC->>AC: @Valid validation on @RequestBody
    
    AC->>AS: updateAddress(addressId, address)
    AS->>AR: findById(addressId)
    AR->>DB: SELECT * FROM addresses WHERE id = ?
    
    alt Address Found
        DB-->>AR: Address entity
        AR-->>AS: Optional<Address> present
        AS->>AS: Update fields: fullName, phoneNumber, street, city, state, postalCode, country
        AS->>AR: save(updatedAddress)
        AR->>DB: UPDATE addresses SET full_name=?, phone_number=?, street=?,<br/>city=?, state=?, postal_code=?, country=? WHERE id = ?
        DB-->>AR: Updated Address
        AR-->>AS: Address
        AS-->>AC: Address
        AC-->>F: 200 OK + Updated Address
    else Address Not Found
        AS-->>AC: throw AddressNotFoundException
        AC-->>F: 404 Not Found
    end`
    },
    {
      id: 'address-delete-flow',
      title: 'Address Deletion (DELETE /api/users/addresses/{addressId})',
      description: 'Delete address - USER can delete their own addresses. Returns 204 No Content.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant AC as AddressController
    participant AS as AddressService
    participant AR as AddressRepository
    participant DB as Database

    F->>AC: DELETE /api/users/addresses/{addressId} + JWT Auth
    AC->>AC: @PreAuthorize("hasRole('USER')")
    
    AC->>AS: deleteAddress(addressId)
    AS->>AR: findById(addressId)
    AR->>DB: SELECT * FROM addresses WHERE id = ?
    
    alt Address Found
        DB-->>AR: Address entity
        AR-->>AS: Address
        AS->>AR: delete(address)
        AR->>DB: DELETE FROM addresses WHERE id = ?
        DB-->>AR: Success
        AR-->>AS: void
        AS-->>AC: void
        AC-->>F: 204 No Content
    else Address Not Found
        AS-->>AC: throw AddressNotFoundException
        AC-->>F: 404 Not Found
    end`
    },
    {
      id: 'store-sequence-title',
      title: 'Store & Order Sequence Diagrams',
      description: 'The following sequence diagrams detail store operations including cart management, order processing, payment, and delivery tracking. Shows complex flows with multiple service interactions, discount validation, and role-based access control.',
      category: 'api-section',
      definition: ''
    },
    {
      id: 'store-cart-add-flow',
      title: 'Add to Cart (POST /api/stores/cart/add/{petId})',
      description: 'Add pet to user cart - Authentication required (USER or ADMIN role).',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant SC as StoreController
    participant US as UserService
    participant CS as CartService
    participant CR as CartRepository
    participant PR as PetRepository
    participant DB as Database

    F->>SC: POST /api/stores/cart/add/{petId} + JWT Auth
    SC->>SC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    SC->>SC: Get email from SecurityContext
    SC->>US: getUserByEmail(userEmail)
    US->>DB: SELECT * FROM users WHERE email = ?
    
    alt User Found
        DB-->>US: User entity with ID
        US-->>SC: Optional<User> present
        SC->>CS: addPetToCart(user.getId(), petId)
        CS->>CR: findByUserId(userId)
        CS->>PR: findById(petId)
        PR->>DB: SELECT * FROM pets WHERE id = ?
        
        alt Pet Found
            DB-->>PR: Pet entity
            PR-->>CS: Optional<Pet> present
            CS->>CS: Add or update CartItem (pet, quantity)
            CS->>CS: Recalculate cart total
            CS->>CR: save(cart)
            CR->>DB: INSERT/UPDATE cart_items, UPDATE cart total
            DB-->>CR: Updated Cart with items
            CR-->>CS: Cart
            CS-->>SC: Cart
            SC-->>F: 200 OK + Updated Cart
        else Pet Not Found
            CS-->>SC: throw PetNotFoundException
            SC-->>F: 404 Not Found "Pet not found"
        end
    else User Not Found
        US-->>SC: Optional<User> empty
        SC-->>F: 400 Bad Request
    end`
    },
    {
      id: 'store-cart-get-flow',
      title: 'Get Cart (GET /api/stores/cart/{userId})',
      description: 'Retrieve user cart with all items and total - Authentication required.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant SC as StoreController
    participant CS as CartService
    participant CR as CartRepository
    participant DB as Database

    F->>SC: GET /api/stores/cart/{userId} + JWT Auth
    SC->>SC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    SC->>CS: getCartByUserId(userId)
    CS->>CR: findByUserId(userId)
    CR->>DB: SELECT c.*, ci.*, p.* FROM carts c<br/>LEFT JOIN cart_items ci ON c.id = ci.cart_id<br/>LEFT JOIN pets p ON ci.pet_id = p.id<br/>WHERE c.user_id = ?
    DB-->>CR: Cart with CartItems and Pets
    CR-->>CS: Optional<Cart>
    
    alt Cart Exists
        CS-->>SC: Cart
        SC-->>F: 200 OK + Cart {items, total}
    else Cart Not Found
        CS->>CS: Create new empty cart for user
        CS->>CR: save(newCart)
        CR->>DB: INSERT INTO carts (user_id, total) VALUES (?, 0)
        DB-->>CR: New Cart
        CR-->>CS: Cart
        CS-->>SC: Empty Cart
        SC-->>F: 200 OK + Empty Cart
    end`
    },
    {
      id: 'store-cart-remove-item-flow',
      title: 'Remove Cart Item (DELETE /api/stores/cart/item/{cartItemId})',
      description: 'Remove specific item from cart - Returns 204 No Content.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant SC as StoreController
    participant CS as CartService
    participant CIR as CartItemRepository
    participant CR as CartRepository
    participant DB as Database

    F->>SC: DELETE /api/stores/cart/item/{cartItemId} + JWT Auth
    SC->>SC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    SC->>CS: removeCartItem(cartItemId)
    CS->>CIR: findById(cartItemId)
    CIR->>DB: SELECT * FROM cart_items WHERE id = ?
    
    alt CartItem Found
        DB-->>CIR: CartItem entity with cart reference
        CIR-->>CS: Optional<CartItem> present
        CS->>CS: Get cart and recalculate total
        CS->>CIR: delete(cartItem)
        CIR->>DB: DELETE FROM cart_items WHERE id = ?
        CS->>CR: save(cart) with updated total
        CR->>DB: UPDATE carts SET total = ? WHERE id = ?
        DB-->>CR: Success
        CR-->>CS: void
        CS-->>SC: void
        SC-->>F: 204 No Content
    else CartItem Not Found
        CS-->>SC: throw CartItemNotFoundException
        SC-->>F: 404 Not Found
    end`
    },
    {
      id: 'store-discount-validate-flow',
      title: 'Validate Discount (GET /api/stores/cart/discount/validate)',
      description: 'Validate discount code and calculate new total - Public or authenticated access.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant SC as StoreController
    participant DS as DiscountService
    participant DR as DiscountRepository
    participant DB as Database

    F->>SC: GET /api/stores/cart/discount/validate?code=SUMMER20&total=100.00
    SC->>SC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    SC->>DS: validateDiscount(code)
    DS->>DR: findByCode(code)
    DR->>DB: SELECT * FROM discounts WHERE code = ?
    
    alt Discount Found
        DB-->>DR: Discount entity (code, percentage, validFrom, validTo, active)
        DR-->>DS: Optional<Discount> present
        DS->>DS: Validate: active = true
        DS->>DS: Validate: current date BETWEEN validFrom AND validTo
        
        alt Valid Discount
            DS-->>SC: Discount {code, percentage}
            SC->>SC: Calculate discountAmount = total * (percentage / 100)
            SC->>SC: Calculate newTotal = total - discountAmount
            SC-->>F: 200 OK + {code, percentage, discountAmount, newTotal}
        else Invalid Discount (expired/inactive)
            DS-->>SC: throw InvalidDiscountException("Discount expired or inactive")
            SC-->>F: 400 Bad Request "Invalid discount code"
        end
    else Discount Not Found
        DR-->>DS: Optional<Discount> empty
        DS-->>SC: throw DiscountNotFoundException("Discount code not found")
        SC-->>F: 404 Not Found "Discount code not found"
    end`
    },
    {
      id: 'store-checkout-flow',
      title: 'Checkout Cart (POST /api/stores/checkout)',
      description: 'Convert cart to order with optional discount - Creates order with PENDING status.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant SC as StoreController
    participant US as UserService
    participant OS as OrderService
    participant DS as DiscountService
    participant CS as CartService
    participant OR as OrderRepository
    participant DB as Database

    F->>SC: POST /api/stores/checkout?discountCode=SUMMER20 + JWT Auth
    SC->>SC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    SC->>SC: Get email from SecurityContext
    SC->>US: getUserByEmail(userEmail)
    US->>DB: SELECT * FROM users WHERE email = ?
    
    alt User Found
        DB-->>US: User entity
        US-->>SC: Optional<User> present
        SC->>OS: checkout(user.getId(), discountCode)
        OS->>CS: getCartByUserId(userId)
        CS->>DB: SELECT cart with items
        
        alt Cart Has Items
            DB-->>CS: Cart with items
            CS-->>OS: Cart
            
            alt Discount Code Provided
                OS->>DS: validateDiscount(discountCode)
                DS->>DB: Validate discount code
                DB-->>DS: Valid Discount
                DS-->>OS: Discount {percentage}
                OS->>OS: Apply discount to cart total
            end
            
            OS->>OS: Create Order from Cart<br/>(copy cart items to order items)<br/>status = PENDING
            OS->>OR: save(order)
            OR->>DB: INSERT INTO orders (user_id, total, discount_id, status)<br/>INSERT INTO order_items (order_id, pet_id, quantity, price)
            DB-->>OR: Saved Order with ID
            OR-->>OS: Order
            OS->>CS: clearCart(userId)
            CS->>DB: DELETE FROM cart_items WHERE cart_id = ?
            OS-->>SC: Order
            SC-->>F: 200 OK + Order {id, items, total, status: PENDING}
        else Empty Cart
            OS-->>SC: throw EmptyCartException
            SC-->>F: 400 Bad Request "Cart is empty"
        end
    else User Not Found
        US-->>SC: Optional<User> empty
        SC-->>F: 400 Bad Request
    end`
    },
    {
      id: 'store-payment-flow',
      title: 'Make Payment (POST /api/stores/order/{orderId}/pay)',
      description: 'Process payment for order - Creates payment record and updates order status to PAID.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant SC as StoreController
    participant US as UserService
    participant OS as OrderService
    participant PS as PaymentService
    participant OR as OrderRepository
    participant PR as PaymentRepository
    participant DB as Database

    F->>SC: POST /api/stores/order/{orderId}/pay + JWT Auth<br/>{paymentMethod, cardNumber, eWalletType}
    SC->>SC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    SC->>SC: @Valid validation on @RequestBody
    SC->>SC: Get email from SecurityContext
    SC->>US: getUserByEmail(userEmail)
    US->>DB: SELECT * FROM users WHERE email = ?
    
    alt User Found
        DB-->>US: User entity
        US-->>SC: Optional<User> present
        SC->>OS: isOrderOwnedByUser(orderId, user.getId())
        OS->>OR: findByIdAndUserId(orderId, userId)
        OR->>DB: SELECT * FROM orders WHERE id = ? AND user_id = ?
        
        alt Order Owned By User
            DB-->>OR: Order entity
            OR-->>OS: true
            OS-->>SC: true
            SC->>OS: makePayment(orderId, paymentRequest)
            OS->>OR: findById(orderId)
            OR->>DB: SELECT * FROM orders WHERE id = ?
            DB-->>OR: Order with total amount
            OR-->>OS: Order
            OS->>OS: Create Payment entity<br/>(amount, method, cardLast4, eWalletType)<br/>status = COMPLETED
            OS->>PR: save(payment)
            PR->>DB: INSERT INTO payments<br/>(order_id, amount, payment_method, card_last4, e_wallet_type, status)
            DB-->>PR: Saved Payment with ID
            PR-->>OS: Payment
            OS->>OS: Update order.status = PAID
            OS->>OS: Update order.payment = payment
            OS->>OR: save(order)
            OR->>DB: UPDATE orders SET status = 'PAID', payment_id = ? WHERE id = ?
            DB-->>OR: Updated Order
            OR-->>OS: Order
            OS-->>SC: Payment
            SC-->>F: 200 OK + Payment details
        else Order Not Owned
            OS-->>SC: throw OrderOwnershipException
            SC-->>F: 403 Forbidden "Order does not belong to user"
        end
    else User Not Found
        US-->>SC: Optional<User> empty
        SC-->>F: 400 Bad Request
    end`
    },
    {
      id: 'store-orders-list-flow',
      title: 'Get Orders (GET /api/stores/orders)',
      description: 'Retrieve orders - ADMIN gets all orders, USER gets only their own orders.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant SC as StoreController
    participant US as UserService
    participant OS as OrderService
    participant OR as OrderRepository
    participant DB as Database

    F->>SC: GET /api/stores/orders + JWT Auth
    SC->>SC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    SC->>SC: Get email from SecurityContext
    SC->>US: getUserByEmail(userEmail)
    US->>DB: SELECT * FROM users WHERE email = ?
    
    alt User Found
        DB-->>US: User entity with roles
        US-->>SC: Optional<User> present
        SC->>SC: Check if user has ROLE_ADMIN
        
        alt Is Admin
            SC->>OS: getAllOrders()
            OS->>OR: findAll()
            OR->>DB: SELECT o.*, oi.*, p.* FROM orders o<br/>LEFT JOIN order_items oi ON o.id = oi.order_id<br/>ORDER BY o.created_at DESC
            DB-->>OR: List<Order> all orders
            OR-->>OS: List<Order>
            OS-->>SC: List<Order>
            SC-->>F: 200 OK + All Orders
        else Is User
            SC->>OS: getOrdersByUserId(user.getId())
            OS->>OR: findByUserId(userId)
            OR->>DB: SELECT o.*, oi.*, p.* FROM orders o<br/>LEFT JOIN order_items oi ON o.id = oi.order_id<br/>WHERE o.user_id = ?<br/>ORDER BY o.created_at DESC
            DB-->>OR: List<Order> user's orders
            OR-->>OS: List<Order>
            OS-->>SC: List<Order>
            SC-->>F: 200 OK + User's Orders
        end
    else User Not Found
        US-->>SC: Optional<User> empty
        SC-->>F: 400 Bad Request
    end`
    },
    {
      id: 'store-order-get-flow',
      title: 'Get Single Order (GET /api/stores/order/{orderId})',
      description: 'Retrieve order details - ADMIN can access any order, USER only their own.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant SC as StoreController
    participant US as UserService
    participant OS as OrderService
    participant OR as OrderRepository
    participant DB as Database

    F->>SC: GET /api/stores/order/{orderId} + JWT Auth
    SC->>SC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    SC->>SC: Get email from SecurityContext
    SC->>US: getUserByEmail(userEmail)
    US->>DB: SELECT * FROM users WHERE email = ?
    
    alt User Found
        DB-->>US: User entity with roles
        US-->>SC: Optional<User> present
        SC->>SC: Check if user has ROLE_ADMIN
        
        alt Is Admin
            SC->>OS: getOrderById(orderId)
            OS->>OR: findById(orderId)
            OR->>DB: SELECT o.*, oi.*, p.*, pay.*, d.* FROM orders o<br/>LEFT JOIN order_items oi ON o.id = oi.order_id<br/>LEFT JOIN payments pay ON o.payment_id = pay.id<br/>LEFT JOIN deliveries d ON o.id = d.order_id<br/>WHERE o.id = ?
            
            alt Order Found
                DB-->>OR: Order with items, payment, delivery
                OR-->>OS: Optional<Order> present
                OS-->>SC: Order
                SC-->>F: 200 OK + Order details
            else Order Not Found
                OR-->>OS: Optional<Order> empty
                OS-->>SC: throw OrderNotFoundException
                SC-->>F: 404 Not Found
            end
        else Is User
            SC->>OS: getOrderByIdAndUserId(orderId, user.getId())
            OS->>OR: findByIdAndUserId(orderId, userId)
            OR->>DB: SELECT o.* FROM orders o WHERE o.id = ? AND o.user_id = ?
            
            alt Order Found and Owned
                DB-->>OR: Order entity
                OR-->>OS: Order
                OS-->>SC: Order
                SC-->>F: 200 OK + Order details
            else Order Not Found/Not Owned
                OR-->>OS: null
                OS-->>SC: throw OrderNotFoundException or OrderOwnershipException
                SC-->>F: 404 Not Found or 403 Forbidden
            end
        end
    else User Not Found
        US-->>SC: Optional<User> empty
        SC-->>F: 400 Bad Request
    end`
    },
    {
      id: 'store-order-cancel-flow',
      title: 'Cancel Order (DELETE /api/stores/order/{orderId})',
      description: 'Cancel order - USER can cancel their own orders. Updates status to CANCELLED.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant SC as StoreController
    participant US as UserService
    participant OS as OrderService
    participant OR as OrderRepository
    participant DB as Database

    F->>SC: DELETE /api/stores/order/{orderId} + JWT Auth
    SC->>SC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    SC->>SC: Get email from SecurityContext
    SC->>US: getUserByEmail(userEmail)
    US->>DB: SELECT * FROM users WHERE email = ?
    
    alt User Found
        DB-->>US: User entity
        US-->>SC: Optional<User> present
        SC->>OS: isOrderOwnedByUser(orderId, user.getId())
        OS->>OR: findByIdAndUserId(orderId, userId)
        OR->>DB: SELECT * FROM orders WHERE id = ? AND user_id = ?
        
        alt Order Owned By User
            DB-->>OR: Order entity
            OR-->>OS: true
            OS-->>SC: true
            SC->>OS: cancelOrder(orderId)
            OS->>OR: findById(orderId)
            OR->>DB: SELECT * FROM orders WHERE id = ?
            DB-->>OR: Order
            OR-->>OS: Order
            OS->>OS: Update order.status = CANCELLED
            OS->>OR: save(order)
            OR->>DB: UPDATE orders SET status = 'CANCELLED' WHERE id = ?
            DB-->>OR: Updated Order
            OR-->>OS: void
            OS-->>SC: void
            SC-->>F: 200 OK + {message: "Order cancelled successfully"}
        else Order Not Owned
            OS-->>SC: throw OrderOwnershipException
            SC-->>F: 403 Forbidden "Order does not belong to user"
        end
    else User Not Found
        US-->>SC: Optional<User> empty
        SC-->>F: 400 Bad Request
    end`
    },
    {
      id: 'store-order-delete-flow',
      title: 'Delete Order (DELETE /api/stores/order/{orderId}/delete)',
      description: 'Permanently delete order - ADMIN can delete any order, USER only their own.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant SC as StoreController
    participant US as UserService
    participant OS as OrderService
    participant OR as OrderRepository
    participant DB as Database

    F->>SC: DELETE /api/stores/order/{orderId}/delete + JWT Auth
    SC->>SC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    SC->>SC: Get email from SecurityContext
    SC->>US: getUserByEmail(userEmail)
    US->>DB: SELECT * FROM users WHERE email = ?
    
    alt User Found
        DB-->>US: User entity with roles
        US-->>SC: Optional<User> present
        SC->>SC: Check if user has ROLE_ADMIN
        
        alt Is Admin
            SC->>OS: deleteOrder(orderId)
            OS->>OR: findById(orderId)
            OS->>OR: delete(order)
            OR->>DB: DELETE FROM orders WHERE id = ?<br/>CASCADE delete order_items, payments, deliveries
            DB-->>OR: Success
            OR-->>OS: void
            OS-->>SC: void
            SC-->>F: 200 OK + {message: "Order deleted successfully"}
        else Is User
            SC->>OS: isOrderOwnedByUser(orderId, user.getId())
            
            alt Order Owned By User
                OS-->>SC: true
                SC->>OS: deleteOrder(orderId)
                OS->>OR: delete(order)
                OR->>DB: DELETE FROM orders WHERE id = ?
                DB-->>OR: Success
                OR-->>OS: void
                OS-->>SC: void
                SC-->>F: 200 OK + {message: "Order deleted successfully"}
            else Order Not Owned
                OS-->>SC: throw OrderOwnershipException
                SC-->>F: 403 Forbidden "Order does not belong to user"
            end
        end
    else User Not Found
        US-->>SC: Optional<User> empty
        SC-->>F: 400 Bad Request
    end`
    },
    {
      id: 'store-delivery-status-update-flow',
      title: 'Update Delivery Status (PATCH /api/stores/order/{orderId}/delivery-status)',
      description: 'Update order delivery status - Admin-only operation. Updates delivery status (PENDING → SHIPPED → DELIVERED).',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant SC as StoreController
    participant OS as OrderService
    participant OR as OrderRepository
    participant DR as DeliveryRepository
    participant DB as Database

    F->>SC: PATCH /api/stores/order/{orderId}/delivery-status + JWT Auth<br/>{status: "SHIPPED", date: "2024-11-02T10:00:00"}
    SC->>SC: @PreAuthorize("hasRole('ADMIN')")
    SC->>SC: @Valid validation on @RequestBody
    SC->>SC: Parse DeliveryStatus from request
    SC->>OS: updateOrderDeliveryStatus(orderId, status, dateString)
    OS->>OR: findById(orderId)
    OR->>DB: SELECT o.*, d.* FROM orders o<br/>LEFT JOIN deliveries d ON o.id = d.order_id<br/>WHERE o.id = ?
    
    alt Order Found
        DB-->>OR: Order with delivery
        OR-->>OS: Optional<Order> present
        
        alt Has Delivery
            OS->>OS: Get delivery from order
            OS->>OS: Update delivery status
            
            alt Status SHIPPED
                OS->>OS: delivery.setStatus(SHIPPED)<br/>delivery.setShippedAt(date)
            else Status DELIVERED
                OS->>OS: delivery.setStatus(DELIVERED)<br/>delivery.setDeliveredAt(date)
            end
            
            OS->>DR: save(delivery)
            DR->>DB: UPDATE deliveries SET status = ?, shipped_at = ?, delivered_at = ?<br/>WHERE id = ?
            DB-->>DR: Updated Delivery
            DR-->>OS: Delivery
            OS-->>SC: void
            SC-->>F: 200 OK + {message: "Order delivery status updated successfully"}
        else No Delivery
            OS-->>SC: throw DeliveryNotFoundException
            SC-->>F: 404 Not Found "Delivery not found for order"
        end
    else Order Not Found
        OR-->>OS: Optional<Order> empty
        OS-->>SC: throw OrderNotFoundException
        SC-->>F: 404 Not Found
    end`
    },
    {
      id: 'discount-sequence-title',
      title: 'Discount Sequence Diagrams',
      description: 'The following sequence diagrams detail discount management operations in the Pet Store API. Shows CRUD operations for discount codes, validation logic for active/expired discounts, and public access for promotional displays.',
      category: 'api-section',
      definition: ''
    },
    {
      id: 'discount-list-flow',
      title: 'Get All Discounts (GET /api/discounts)',
      description: 'Retrieve all discounts - Admin-only operation for discount management.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant DC as DiscountController
    participant DS as DiscountService
    participant DR as DiscountRepository
    participant DB as Database

    F->>DC: GET /api/discounts + JWT Auth
    DC->>DC: @PreAuthorize("hasRole('ADMIN')")
    DC->>DS: getAllDiscounts()
    DS->>DR: findAll()
    DR->>DB: SELECT * FROM discounts ORDER BY created_at DESC
    DB-->>DR: List<Discount> (all fields including active, validFrom, validTo)
    DR-->>DS: List<Discount>
    DS-->>DC: List<Discount>
    DC-->>F: 200 OK + List of all discounts`
    },
    {
      id: 'discount-get-flow',
      title: 'Get Discount by ID (GET /api/discounts/{id})',
      description: 'Retrieve single discount - Admin-only.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant DC as DiscountController
    participant DS as DiscountService
    participant DR as DiscountRepository
    participant DB as Database

    F->>DC: GET /api/discounts/{id} + JWT Auth
    DC->>DC: @PreAuthorize("hasRole('ADMIN')")
    DC->>DS: getDiscountById(id)
    DS->>DR: findById(id)
    DR->>DB: SELECT * FROM discounts WHERE id = ?
    
    alt Discount Found
        DB-->>DR: Discount entity
        DR-->>DS: Optional<Discount> present
        DS-->>DC: Discount
        DC-->>F: 200 OK + Discount details
    else Discount Not Found
        DR-->>DS: Optional<Discount> empty
        DS-->>DC: throw DiscountNotFoundException
        DC-->>F: 404 Not Found
    end`
    },
    {
      id: 'discount-create-flow',
      title: 'Create Discount (POST /api/discounts)',
      description: 'Create new discount code - Admin-only operation with validation.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant DC as DiscountController
    participant DS as DiscountService
    participant DR as DiscountRepository
    participant DB as Database

    F->>DC: POST /api/discounts + JWT Auth<br/>{code, percentage, validFrom, validTo, description, active}
    DC->>DC: @PreAuthorize("hasRole('ADMIN')")
    DC->>DC: @Valid validation on @RequestBody
    
    alt Validation Fails
        DC-->>F: 400 Bad Request<br/>Validation errors (code max 20 chars, etc.)
    else Validation Passes
        DC->>DS: saveDiscount(discount)
        DS->>DS: Validate discount fields
        DS->>DR: save(discount)
        DR->>DB: INSERT INTO discounts<br/>(code, percentage, valid_from, valid_to, description, active)<br/>@CreatedDate, @CreatedBy auto-populated
        DB-->>DR: Saved Discount with ID and audit fields
        DR-->>DS: Discount
        DS-->>DC: Discount
        DC-->>F: 200 OK + Created Discount
    end`
    },
    {
      id: 'discount-update-flow',
      title: 'Update Discount (PUT /api/discounts/{id})',
      description: 'Update existing discount - Admin-only with field validation.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant DC as DiscountController
    participant DS as DiscountService
    participant DR as DiscountRepository
    participant DB as Database

    F->>DC: PUT /api/discounts/{id} + JWT Auth<br/>{code, percentage, validFrom, validTo, description, active}
    DC->>DC: @PreAuthorize("hasRole('ADMIN')")
    DC->>DC: @Valid validation on @RequestBody
    DC->>DS: updateDiscount(id, discount)
    DS->>DR: findById(id)
    DR->>DB: SELECT * FROM discounts WHERE id = ?
    
    alt Discount Found
        DB-->>DR: Discount entity
        DR-->>DS: Optional<Discount> present
        DS->>DS: Update fields: code, percentage, validFrom, validTo, description, active
        DS->>DR: save(updatedDiscount)
        DR->>DB: UPDATE discounts SET code=?, percentage=?, valid_from=?,<br/>valid_to=?, description=?, active=?, updated_at=NOW()<br/>WHERE id = ?
        DB-->>DR: Updated Discount with @LastModifiedDate
        DR-->>DS: Discount
        DS-->>DC: Discount
        DC-->>F: 200 OK + Updated Discount
    else Discount Not Found
        DR-->>DS: Optional<Discount> empty
        DS-->>DC: throw DiscountNotFoundException
        DC-->>F: 404 Not Found
    end`
    },
    {
      id: 'discount-delete-flow',
      title: 'Delete Discount (DELETE /api/discounts/{id})',
      description: 'Delete discount - Admin-only operation. Returns 204 No Content.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant DC as DiscountController
    participant DS as DiscountService
    participant DR as DiscountRepository
    participant DB as Database

    F->>DC: DELETE /api/discounts/{id} + JWT Auth
    DC->>DC: @PreAuthorize("hasRole('ADMIN')")
    DC->>DS: deleteDiscount(id)
    DS->>DR: findById(id)
    DR->>DB: SELECT * FROM discounts WHERE id = ?
    
    alt Discount Found
        DB-->>DR: Discount entity
        DR-->>DS: Discount
        DS->>DR: delete(discount)
        DR->>DB: DELETE FROM discounts WHERE id = ?
        DB-->>DR: Success
        DR-->>DS: void
        DS-->>DC: void
        DC-->>F: 204 No Content
    else Discount Not Found
        DR-->>DS: throw DiscountNotFoundException
        DS-->>DC: DiscountNotFoundException
        DC-->>F: 404 Not Found
    end`
    },
    {
      id: 'discount-validate-public-flow',
      title: 'Validate Discount Code (GET /api/discounts/validate)',
      description: 'Public validation of discount code - Checks if code is valid, active, and within date range.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant DC as DiscountController
    participant DS as DiscountService
    participant DR as DiscountRepository
    participant DB as Database

    F->>DC: GET /api/discounts/validate?code=SUMMER20
    DC->>DS: validateDiscount(code)
    DS->>DR: findByCode(code)
    DR->>DB: SELECT * FROM discounts WHERE code = ?
    
    alt Discount Found
        DB-->>DR: Discount entity
        DR-->>DS: Optional<Discount> present
        DS->>DS: Validate active = true
        DS->>DS: Validate NOW() BETWEEN validFrom AND validTo
        
        alt Valid Discount
            DS-->>DC: Discount
            DC-->>F: 200 OK + Discount {code, percentage, validFrom, validTo}
        else Invalid Discount (expired or inactive)
            DS-->>DC: throw InvalidDiscountException("Discount expired or inactive")
            DC-->>F: 400 Bad Request "Invalid discount code"
        end
    else Discount Not Found
        DR-->>DS: Optional<Discount> empty
        DS-->>DC: throw DiscountNotFoundException("Discount code not found")
        DC-->>F: 404 Not Found "Discount code not found"
    end`
    },
    {
      id: 'discount-active-list-flow',
      title: 'Get Active Discounts (GET /api/discounts/active)',
      description: 'List all active discounts for promotional display - Authenticated users can view available promotions.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant DC as DiscountController
    participant DS as DiscountService
    participant DR as DiscountRepository
    participant DB as Database

    F->>DC: GET /api/discounts/active + JWT Auth
    DC->>DC: @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    DC->>DS: getAllActiveDiscounts()
    DS->>DR: findByActiveTrue()
    DR->>DB: SELECT * FROM discounts<br/>WHERE active = true<br/>AND NOW() BETWEEN valid_from AND valid_to<br/>ORDER BY created_at DESC
    DB-->>DR: List<Discount> active discounts
    DR-->>DS: List<Discount>
    DS-->>DC: List<Discount>
    DC-->>F: 200 OK + List of active promotional discounts`
    },
    {
      id: 'category-sequence-title',
      title: 'Category Sequence Diagrams',
      description: 'The following sequence diagrams detail category management operations for the Pet Store API. Shows CRUD operations with public read access and admin-only write operations, including validation to prevent deletion of categories in use.',
      category: 'api-section',
      definition: ''
    },
    {
      id: 'category-list-flow',
      title: 'Get All Categories (GET /api/categories)',
      description: 'Retrieve all categories - Public access, no authentication required.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant CC as CategoryController
    participant CS as CategoryService
    participant CR as CategoryRepository
    participant DB as Database

    F->>CC: GET /api/categories
    CC->>CS: getAllCategories()
    CS->>CR: findAll()
    CR->>DB: SELECT * FROM categories ORDER BY name
    DB-->>CR: List<Category>
    CR-->>CS: List<Category>
    CS-->>CC: List<Category>
    CC-->>F: 200 OK + List of categories`
    },
    {
      id: 'category-get-flow',
      title: 'Get Category by ID (GET /api/categories/{id})',
      description: 'Retrieve single category - Admin-only.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant CC as CategoryController
    participant CS as CategoryService
    participant CR as CategoryRepository
    participant DB as Database

    F->>CC: GET /api/categories/{id} + JWT Auth
    CC->>CC: @PreAuthorize("hasRole('ADMIN')")
    CC->>CS: getCategoryById(id)
    CS->>CR: findById(id)
    CR->>DB: SELECT * FROM categories WHERE id = ?
    
    alt Category Found
        DB-->>CR: Category entity
        CR-->>CS: Optional<Category> present
        CS-->>CC: Category
        CC-->>F: 200 OK + Category details
    else Category Not Found
        CR-->>CS: Optional<Category> empty
        CS-->>CC: throw CategoryNotFoundException
        CC-->>F: 404 Not Found
    end`
    },
    {
      id: 'category-create-flow',
      title: 'Create Category (POST /api/categories)',
      description: 'Create new category - Admin-only operation. Returns 201 Created.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant CC as CategoryController
    participant CS as CategoryService
    participant CR as CategoryRepository
    participant DB as Database

    F->>CC: POST /api/categories + JWT Auth<br/>{name, description}
    CC->>CC: @PreAuthorize("hasRole('ADMIN')")
    CC->>CC: @Valid validation on @RequestBody
    
    alt Validation Fails
        CC-->>F: 400 Bad Request<br/>Validation errors (name required, etc.)
    else Validation Passes
        CC->>CS: saveCategory(category)
        CS->>CR: save(category)
        CR->>DB: INSERT INTO categories (name, description)<br/>@CreatedDate, @CreatedBy auto-populated
        DB-->>CR: Saved Category with ID and audit fields
        CR-->>CS: Category
        CS-->>CC: Category
        CC-->>F: 201 Created + Category
    end`
    },
    {
      id: 'category-update-flow',
      title: 'Update Category (PUT /api/categories/{id})',
      description: 'Update existing category - Admin-only with validation.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant CC as CategoryController
    participant CS as CategoryService
    participant CR as CategoryRepository
    participant DB as Database

    F->>CC: PUT /api/categories/{id} + JWT Auth<br/>{name, description}
    CC->>CC: @PreAuthorize("hasRole('ADMIN')")
    CC->>CC: @Valid validation on @RequestBody
    CC->>CS: updateCategory(id, categoryDetails)
    CS->>CR: findById(id)
    CR->>DB: SELECT * FROM categories WHERE id = ?
    
    alt Category Found
        DB-->>CR: Category entity
        CR-->>CS: Optional<Category> present
        CS->>CS: Update fields: name, description
        CS->>CR: save(updatedCategory)
        CR->>DB: UPDATE categories SET name=?, description=?, updated_at=NOW()<br/>WHERE id = ?
        DB-->>CR: Updated Category with @LastModifiedDate
        CR-->>CS: Category
        CS-->>CC: Category
        CC-->>F: 200 OK + Updated Category
    else Category Not Found
        CR-->>CS: Optional<Category> empty or null
        CS-->>CC: null
        CC-->>F: 404 Not Found
    end`
    },
    {
      id: 'category-delete-flow',
      title: 'Delete Category (DELETE /api/categories/{id})',
      description: 'Delete category - Admin-only. Cannot delete if category is being used by pets. Returns 204 No Content.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant CC as CategoryController
    participant CS as CategoryService
    participant CR as CategoryRepository
    participant PR as PetRepository
    participant DB as Database

    F->>CC: DELETE /api/categories/{id} + JWT Auth
    CC->>CC: @PreAuthorize("hasRole('ADMIN')")
    CC->>CS: deleteCategory(id)
    CS->>CR: findById(id)
    CR->>DB: SELECT * FROM categories WHERE id = ?
    
    alt Category Found
        DB-->>CR: Category entity
        CR-->>CS: Category
        CS->>PR: existsByCategoryId(id)
        PR->>DB: SELECT COUNT(*) FROM pets WHERE category_id = ?
        
        alt No Pets Using Category
            DB-->>PR: 0 (no pets)
            PR-->>CS: false
            CS->>CR: delete(category)
            CR->>DB: DELETE FROM categories WHERE id = ?
            DB-->>CR: Success
            CR-->>CS: void
            CS-->>CC: void
            CC-->>F: 204 No Content
        else Pets Using Category
            DB-->>PR: count > 0
            PR-->>CS: true
            CS-->>CC: throw CategoryInUseException
            CC-->>F: 400 Bad Request<br/>"Cannot delete category - pets are using it"
        end
    else Category Not Found
        CR-->>CS: throw CategoryNotFoundException
        CS-->>CC: CategoryNotFoundException
        CC-->>F: 404 Not Found
    end`
    },
    {
      id: 'auth-sequence-title',
      title: 'Authentication Sequence Diagrams',
      description: 'The following sequence diagrams detail user authentication and registration flows in the Pet Store API. Shows JWT token generation, Spring Security authentication, password encoding, and user registration with role assignment.',
      category: 'api-section',
      definition: ''
    },
    {
      id: 'auth-login-flow',
      title: 'User Login (POST /api/auth/login)',
      description: 'Authenticate user and return JWT token - Public endpoint.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant AC as AuthController
    participant AM as AuthenticationManager
    participant UDS as UserDetailsService
    participant UR as UserRepository
    participant PE as PasswordEncoder
    participant JWT as JwtTokenProvider
    participant US as UserService
    participant DB as Database

    F->>AC: POST /api/auth/login<br/>{email, password}
    AC->>AC: @Valid validation on @RequestBody
    
    alt Validation Fails
        AC-->>F: 400 Bad Request<br/>Validation errors
    else Validation Passes
        AC->>AM: authenticate(UsernamePasswordAuthenticationToken)
        AM->>UDS: loadUserByUsername(email)
        UDS->>UR: findByEmail(email)
        UR->>DB: SELECT * FROM users WHERE email = ?
        
        alt User Found
            DB-->>UR: User entity with password hash and roles
            UR-->>UDS: User
            UDS-->>AM: UserDetails
            AM->>PE: matches(rawPassword, encodedPassword)
            
            alt Password Match
                PE-->>AM: true
                AM-->>AC: Authentication object
                AC->>JWT: generateToken(authentication)
                JWT->>JWT: Create JWT with claims (email, roles)<br/>Sign with secret key, set expiration
                JWT-->>AC: JWT token string
                AC->>US: getUserByEmail(email)
                US->>UR: findByEmail(email)
                UR->>DB: SELECT * FROM users WHERE email = ?
                DB-->>UR: User entity
                UR-->>US: Optional<User> present
                US-->>AC: User
                AC->>AC: Build response {token, type: "Bearer", user: {id, email, firstName, lastName, roles}}
                AC-->>F: 200 OK + JWT token + User details
            else Password Mismatch
                PE-->>AM: false
                AM-->>AC: throw BadCredentialsException
                AC-->>F: 401 Unauthorized<br/>"Invalid email or password"
            end
        else User Not Found
            UR-->>UDS: Optional<User> empty
            UDS-->>AM: throw UsernameNotFoundException
            AM-->>AC: throw BadCredentialsException
            AC-->>F: 401 Unauthorized<br/>"Invalid email or password"
        end
    end`
    },
    {
      id: 'auth-register-flow',
      title: 'User Registration (POST /api/auth/register)',
      description: 'Register new user account with role assignment - Public endpoint.',
      category: 'api',
      definition: `sequenceDiagram
    participant F as Frontend
    participant AC as AuthController
    participant US as UserService
    participant UR as UserRepository
    participant PE as PasswordEncoder
    participant DB as Database

    F->>AC: POST /api/auth/register<br/>{email, password, firstName, lastName, role}
    AC->>AC: @Valid validation on @RequestBody
    
    alt Validation Fails
        AC-->>F: 400 Bad Request<br/>Validation errors (email format, password strength, etc.)
    else Validation Passes
        AC->>US: existsByEmail(email)
        US->>UR: existsByEmail(email)
        UR->>DB: SELECT COUNT(*) FROM users WHERE email = ?
        
        alt Email Already Exists
            DB-->>UR: count > 0
            UR-->>US: true
            US-->>AC: true
            AC-->>F: 400 Bad Request<br/>{message: "Email is already in use!"}
        else Email Available
            DB-->>UR: count = 0
            UR-->>US: false
            US-->>AC: false
            AC->>PE: encode(password)
            PE-->>AC: Encoded password hash
            AC->>AC: Create User entity<br/>(email, encodedPassword, firstName, lastName)
            
            alt Role is ADMIN
                AC->>AC: user.setRoles(Set.of(Role.ADMIN))
            else Role is USER or not specified
                AC->>AC: user.setRoles(Set.of(Role.USER))
            end
            
            AC->>US: saveUser(user)
            US->>UR: save(user)
            UR->>DB: INSERT INTO users<br/>(email, password, first_name, last_name)<br/>INSERT INTO user_roles (user_id, role)<br/>@CreatedDate auto-populated
            DB-->>UR: Saved User with ID and audit fields
            UR-->>US: User
            US-->>AC: User
            AC-->>F: 200 OK + {message: "User registered successfully!"}
        end
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
      description: 'Complete user experience flow from landing page to order completion, including authentication, pet browsing, cart management, discount application, checkout, and payment processing.',
      category: 'user-flow',
      definition: `flowchart TD
    A[🏠 Landing Page] --> B{User Logged In?}
    B -->|No| C[🔐 Login/Register]
    B -->|Yes| D[📋 Browse Latest Pets]
    
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
    F -->|Yes| H[🛒 Add to Cart]
    G --> D1

    H --> CART[🛒 Shopping Cart]
    CART --> CART1{Continue Shopping?}
    CART1 -->|Yes| D1
    CART1 -->|No| CART2[📦 Review Cart Items]
    
    CART2 --> DISC{Apply Discount?}
    DISC -->|Yes| DISC1[🏷️ Enter Discount Code]
    DISC1 --> DISC2{Valid Code?}
    DISC2 -->|No| DISC3[❌ Invalid Code]
    DISC3 --> CART2
    DISC2 -->|Yes| DISC4[✅ Discount Applied]
    DISC4 --> CHECKOUT
    DISC -->|No| CHECKOUT
    
    CHECKOUT[💳 Proceed to Checkout]
    CHECKOUT --> ADDR{Have Address?}
    ADDR -->|No| ADDR1[📍 Add Delivery Address]
    ADDR1 --> ADDR2[📝 Fill Address Form]
    ADDR2 --> ADDR3{Valid Address?}
    ADDR3 -->|No| ADDR4[❌ Show Errors]
    ADDR4 --> ADDR2
    ADDR3 -->|Yes| ORDER
    ADDR -->|Yes| ORDER
    
    ORDER[📋 Create Order]
    ORDER --> PAY[💰 Payment Method]
    PAY --> PAY1{Payment Type?}
    PAY1 -->|Credit Card| PAY2[💳 Enter Card Details]
    PAY1 -->|E-Wallet| PAY3[📱 Select E-Wallet Type]
    PAY1 -->|Cash| PAY4[💵 Cash on Delivery]
    
    PAY2 --> PAY5[✅ Process Payment]
    PAY3 --> PAY5
    PAY4 --> PAY5
    
    PAY5 --> PAY6{Payment Success?}
    PAY6 -->|No| PAY7[❌ Payment Failed]
    PAY7 --> PAY
    PAY6 -->|Yes| SUCCESS[🎉 Order Confirmed]
    
    SUCCESS --> TRACK[📦 Track Order]
    TRACK --> TRACK1[🚚 Delivery Status]
    TRACK1 --> TRACK2{Status?}
    TRACK2 -->|PENDING| TRACK3[⏳ Order Processing]
    TRACK2 -->|SHIPPED| TRACK4[🚛 Out for Delivery]
    TRACK2 -->|DELIVERED| TRACK5[✅ Order Delivered]
    
    D --> MYPETS[🐾 My Pets]
    MYPETS --> L[➕ Add New Pet]
    L --> M[📝 Pet Form]
    M --> N[📤 Submit Pet Data]
    N --> O{Validation OK?}
    O -->|No| P[❌ Show Errors]
    P --> M
    O -->|Yes| Q[✅ Pet Added]
    Q --> MYPETS
    
    MYPETS --> EDIT[✏️ Edit My Pet]
    EDIT --> EDIT1[📝 Update Pet Details]
    EDIT1 --> EDIT2{Save Changes?}
    EDIT2 -->|Yes| EDIT3[✅ Pet Updated]
    EDIT2 -->|No| MYPETS
    EDIT3 --> MYPETS

    classDef startEnd fill:#e1f5fe
    classDef process fill:#f3e5f5
    classDef decision fill:#fff3e0
    classDef success fill:#e8f5e8
    classDef error fill:#ffebee
    classDef cart fill:#fff9c4
    classDef payment fill:#e0f2f1

    class A,SUCCESS,Q,EDIT3,TRACK5 startEnd
    class C1,D1,D2,CART2,DISC1,ADDR2,M,N,EDIT1,PAY2,PAY3,PAY4 process
    class B,C2,F,CART1,DISC,DISC2,ADDR,ADDR3,O,PAY1,PAY6,EDIT2,TRACK2 decision
    class SUCCESS,Q,DISC4,EDIT3,TRACK5 success
    class C3,G,P,DISC3,ADDR4,PAY7 error
    class CART,CHECKOUT cart
    class PAY,PAY5,ORDER payment`
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
    },
    {
      id: 'github-actions-workflow',
      title: 'GitHub Actions CI/CD Pipeline',
      description: 'Automated build, test, and deployment pipeline stages with parallel job execution and deployment to production environment.',
      category: 'deployment',
      definition: `graph LR
    subgraph "CI/CD Pipeline"
        Commit[Git Push/PR] --> Build[Build Stage]
        Build --> Test[Test Stage]
        Test --> Quality[Code Quality Check]
        Quality --> Package[Package Stage]
        Package --> Deploy[Deploy Stage]
    end
    
    subgraph "Build Stage"
        B1[Checkout Code]
        B2[Setup Java 17]
        B3[Setup Node.js 18]
        B4[Cache Dependencies]
        B5[Build Backend<br/>mvn clean package]
        B6[Build Frontend<br/>npm run build]
    end
    
    subgraph "Test Stage"
        T1[Run Backend Tests<br/>mvn test]
        T2[Run Frontend Tests<br/>npm test]
        T3[Generate Coverage<br/>jacoco:report]
    end
    
    subgraph "Package Stage"
        P1[Build Docker Images]
        P2[Tag Images<br/>version/commit SHA]
        P3[Security Scanning<br/>Trivy]
    end
    
    subgraph "Deploy Stage"
        D1[Push to Registry<br/>Docker Hub/ECR]
        D2[Deploy to Environment<br/>Dev/Staging/Prod]
        D3[Run Smoke Tests]
        D4[Update Status]
    end
    
    Build --> B1 --> B2 --> B3 --> B4 --> B5 --> B6
    Test --> T1 --> T2 --> T3
    Package --> P1 --> P2 --> P3
    Deploy --> D1 --> D2 --> D3 --> D4
    
    classDef commit fill:#4caf50,stroke:#2e7d32,stroke-width:3px,color:#fff
    classDef build fill:#2196f3,stroke:#1565c0,stroke-width:2px,color:#fff
    classDef test fill:#ff9800,stroke:#e65100,stroke-width:2px,color:#fff
    classDef quality fill:#9c27b0,stroke:#6a1b9a,stroke-width:2px,color:#fff
    classDef package fill:#00bcd4,stroke:#00838f,stroke-width:2px,color:#fff
    classDef deploy fill:#f44336,stroke:#c62828,stroke-width:3px,color:#fff
    
    class Commit commit
    class Build,B1,B2,B3,B4,B5,B6 build
    class Test,T1,T2,T3 test
    class Quality quality
    class Package,P1,P2,P3 package
    class Deploy,D1,D2,D3,D4 deploy`
    },
    {
      id: 'cicd-workflow-sequence',
      title: 'CI/CD Workflow Sequence',
      description: 'End-to-end CI/CD process flow from developer push to production deployment with automated testing, Docker image building, and deployment verification.',
      category: 'deployment',
      definition: `sequenceDiagram
    participant Dev as Developer
    participant Git as GitHub
    participant CI as CI/CD Pipeline
    participant Test as Test Suite
    participant Docker as Docker Build
    participant Reg as Docker Registry
    participant Prod as Production Server

    Dev->>Git: Push code to branch
    Git->>CI: Trigger GitHub Actions
    CI->>CI: Checkout code
    
    Note over CI: Build Stage
    CI->>CI: Setup Java 17 & Node.js 18
    CI->>CI: Cache Maven & npm dependencies
    CI->>CI: Build backend (mvn clean package)
    CI->>CI: Build frontend (npm run build)
    
    Note over CI,Test: Test Stage
    CI->>Test: Run backend tests (mvn test)
    Test->>Test: Execute JUnit tests
    Test->>Test: Generate JaCoCo coverage
    Test-->>CI: Backend test results
    
    CI->>Test: Run frontend tests (npm test)
    Test->>Test: Execute Jasmine/Karma tests
    Test->>Test: Generate coverage report
    Test-->>CI: Frontend test results
    
    alt Tests Pass
        Note over CI,Docker: Package Stage
        CI->>Docker: Build Docker images
        Docker->>Docker: Build petstore-backend image
        Docker->>Docker: Build petstore-frontend image
        Docker->>Docker: Tag images (SHA/version)
        Docker->>Docker: Run Trivy security scan
        Docker-->>CI: Images ready
        
        CI->>Reg: Push images to registry
        Reg-->>CI: Confirm push successful
        
        alt Main Branch (Production)
            Note over CI,Prod: Deploy Stage
            CI->>Prod: Trigger deployment
            Prod->>Reg: Pull latest images
            Reg-->>Prod: Download images
            Prod->>Prod: Stop old containers
            Prod->>Prod: Start new containers
            Prod->>Prod: Run health checks
            
            alt Deployment Success
                Prod-->>CI: Deployment successful
                CI->>CI: Run smoke tests
                CI-->>Git: Update commit status ✅
                Git-->>Dev: Notify: Deployed to Production
            else Deployment Failed
                Prod-->>CI: Deployment failed
                CI->>Prod: Rollback to previous version
                CI-->>Git: Update commit status ❌
                Git-->>Dev: Notify: Deployment failed
            end
            
        else Feature/Develop Branch
            CI-->>Git: Update PR status ✅
            Git-->>Dev: Notify: Tests passed, ready for review
        end
        
    else Tests Fail
        CI-->>Git: Report test failures ❌
        Git-->>Dev: Notify: Tests failed, fix required
    end`
    },
    {
      id: 'ong-design-pattern',
      title: 'Order Number Generator Strategy Pattern',
      description: 'Overview of the strategy design pattern used in the order number generator.',
      category: 'design-patterns',
      definition: `classDiagram
    %% Strategy Pattern Interface
    class OrderNumberGenerator {
        <<interface>>
        +generate() String
    }

    %% Concrete Implementations
    class UUIDOrderNumberGenerator {
        +generate() String
    }

    class SequentialOrderNumberGenerator {
        -AtomicLong counter
        +generate() String
    }

    class TimeBasedOrderNumberGenerator {
        -Clock clock
        -SecureRandom random
        +TimeBasedOrderNumberGenerator(Clock)
        +generate() String
    }

    %% Configuration Classes
    class OrderConfiguration {
        +clock() Clock
        +orderNumberGenerator(OrderNumberGenerator) OrderNumberGenerator
    }

    class OrderGeneratorConfig {
        -String generatorType
        +clock() Clock
        +configuredOrderNumberGenerator(...) OrderNumberGenerator
    }

    %% Service Layer
    class OrderService {
        -OrderNumberGenerator orderNumberGenerator
        +OrderService(OrderNumberGenerator)
        +order.setOrderNumber() void
    }

    %% Spring Components
    class Clock {
        <<Java Time API>>
        +millis() long
        +systemDefaultZone() Clock
    }

    class SecureRandom {
        <<Java Security>>
        +nextInt(int) int
    }

    class AtomicLong {
        <<Java Concurrent>>
        +incrementAndGet() long
    }

    %% Relationships - Strategy Pattern
    OrderNumberGenerator <|.. UUIDOrderNumberGenerator : implements
    OrderNumberGenerator <|.. SequentialOrderNumberGenerator : implements
    OrderNumberGenerator <|.. TimeBasedOrderNumberGenerator : implements

    %% Dependencies
    OrderService --> OrderNumberGenerator : uses
    TimeBasedOrderNumberGenerator --> Clock : depends on
    TimeBasedOrderNumberGenerator --> SecureRandom : depends on
    SequentialOrderNumberGenerator --> AtomicLong : contains

    %% Configuration Dependencies
    OrderConfiguration ..> OrderNumberGenerator : configures
    OrderConfiguration ..> Clock : creates
    OrderGeneratorConfig ..> OrderNumberGenerator : configures
    OrderGeneratorConfig ..> Clock : creates
    OrderGeneratorConfig ..> UUIDOrderNumberGenerator : selects
    OrderGeneratorConfig ..> SequentialOrderNumberGenerator : selects
    OrderGeneratorConfig ..> TimeBasedOrderNumberGenerator : selects

    %% Spring Annotations
    note for UUIDOrderNumberGenerator "@Component<br>@Qualifier('uuidOrderNumberGenerator')"
    note for SequentialOrderNumberGenerator "@Component<br>@Qualifier('sequentialOrderNumberGenerator')"
    note for TimeBasedOrderNumberGenerator "@Component<br>@Qualifier('timeBasedOrderNumberGenerator')"
    note for OrderService "@Service<br>Constructor Injection"
    note for OrderConfiguration "@Configuration<br>@Primary bean definition"
    note for OrderGeneratorConfig "@Configuration<br>Property-based selection"`
    },
    {
      id: 'payment-type-design-pattern',
      title: 'Payment Type Strategy Pattern',
      description: 'Overview of the strategy design pattern used in the payment processing.',
      category: 'design-patterns',
      definition: `classDiagram
    %% Enums
    class PaymentType {
        <<enumeration>>
        CREDIT_CARD
        DEBIT_CARD
        E_WALLET
        PAYPAL
    }
    
    class WalletType {
        <<enumeration>>
        GRABPAY
        BOOSTPAY
        TOUCHNGO
    }
    
    class PaymentStatus {
        <<enumeration>>
        PENDING
        SUCCESS
        FAILED
    }
    
    %% Entities
    class Payment {
        -Long id
        -Order order
        -BigDecimal amount
        -PaymentStatus status
        -PaymentType paymentType
        -String paymentNote
        -LocalDateTime paidAt
    }
    
    class Order {
        -Long id
        -BigDecimal totalAmount
        -List~OrderItem~ items
    }
    
    %% DTOs
    class PaymentOrderRequest {
        -PaymentType paymentType
        -WalletType walletType
        -String paymentNote
        -String cardNumber
        -String paypalId
        -String walletId
        -Long shippingAddressId
        -Long billingAddressId
    }
    
    %% Strategy Interfaces
    class PaymentStrategy {
        <<interface>>
        +getPaymentType() PaymentType
        +processPayment(Payment, PaymentOrderRequest)
        +validatePayment(PaymentOrderRequest)
    }
    
    class EWalletStrategy {
        <<interface>>
        +getWalletType() WalletType
        +processEWalletPayment(Payment, PaymentOrderRequest)
        +validateEWalletPayment(PaymentOrderRequest)
    }
    
    %% Payment Strategy Implementations
    class CreditCardPaymentStrategy {
        <<Component>>
        +getPaymentType() PaymentType
        +processPayment(Payment, PaymentOrderRequest)
        +validatePayment(PaymentOrderRequest)
    }
    
    class DebitCardPaymentStrategy {
        <<Component>>
        +getPaymentType() PaymentType
        +processPayment(Payment, PaymentOrderRequest)
        +validatePayment(PaymentOrderRequest)
    }
    
    class PayPalPaymentStrategy {
        <<Component>>
        +getPaymentType() PaymentType
        +processPayment(Payment, PaymentOrderRequest)
        +validatePayment(PaymentOrderRequest)
    }
    
    class EWalletPaymentStrategy {
        <<Component>>
        -EWalletStrategyFactory eWalletStrategyFactory
        +getPaymentType() PaymentType
        +processPayment(Payment, PaymentOrderRequest)
        +validatePayment(PaymentOrderRequest)
    }
    
    %% E-Wallet Strategy Implementations
    class GrabPayStrategy {
        <<Component>>
        +getWalletType() WalletType
        +processEWalletPayment(Payment, PaymentOrderRequest)
        +validateEWalletPayment(PaymentOrderRequest)
    }
    
    class BoostPayStrategy {
        <<Component>>
        +getWalletType() WalletType
        +processEWalletPayment(Payment, PaymentOrderRequest)
        +validateEWalletPayment(PaymentOrderRequest)
    }
    
    class TouchNGoStrategy {
        <<Component>>
        +getWalletType() WalletType
        +processEWalletPayment(Payment, PaymentOrderRequest)
        +validateEWalletPayment(PaymentOrderRequest)
    }
    
    %% Factories
    class PaymentStrategyFactory {
        <<Component>>
        -Map~PaymentType, PaymentStrategy~ strategies
        +PaymentStrategyFactory(List~PaymentStrategy~)
        +getStrategy(PaymentType) PaymentStrategy
    }
    
    class EWalletStrategyFactory {
        <<Component>>
        -Map~WalletType, EWalletStrategy~ strategies
        +EWalletStrategyFactory(List~EWalletStrategy~)
        +getStrategy(WalletType) EWalletStrategy
    }
    
    %% Service
    class OrderService {
        <<Service>>
        -OrderRepository orderRepository
        -PaymentRepository paymentRepository
        -PaymentStrategyFactory paymentStrategyFactory
        +makePayment(Long, PaymentOrderRequest) Payment
    }
    
    %% Relationships - Strategy Pattern
    PaymentStrategy <|.. CreditCardPaymentStrategy : implements
    PaymentStrategy <|.. DebitCardPaymentStrategy : implements
    PaymentStrategy <|.. PayPalPaymentStrategy : implements
    PaymentStrategy <|.. EWalletPaymentStrategy : implements
    
    EWalletStrategy <|.. GrabPayStrategy : implements
    EWalletStrategy <|.. BoostPayStrategy : implements
    EWalletStrategy <|.. TouchNGoStrategy : implements
    
    %% Factory Relationships
    PaymentStrategyFactory o-- PaymentStrategy : manages
    EWalletStrategyFactory o-- EWalletStrategy : manages
    
    %% Nested Strategy
    EWalletPaymentStrategy --> EWalletStrategyFactory : uses
    
    %% Service Relationships
    OrderService --> PaymentStrategyFactory : uses
    OrderService --> Payment : creates
    OrderService --> PaymentOrderRequest : receives
    OrderService --> Order : manages
    
    %% Entity Relationships
    Payment --> PaymentType : has
    Payment --> PaymentStatus : has
    Payment --> WalletType : has (optional)
    Payment --> Order : belongs to
    
    %% DTO Relationships
    PaymentOrderRequest --> PaymentType : specifies
    PaymentOrderRequest --> WalletType : specifies (optional)`
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