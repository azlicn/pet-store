# 🚀 Pawfect Store - Backend API

Spring Boot 3.2 REST API for Pawfect Store pet e-commerce platform.

## 📋 Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [API Documentation](#api-documentation)
- [Design Patterns](#design-patterns)
- [Database](#database)
- [Security](#security)

---

## 🎯 Overview

The Pawfect Store backend is a RESTful API built with Spring Boot 3.2, featuring:

- **JWT Authentication** - Secure token-based authentication
- **Role-Based Authorization** - USER and ADMIN roles
- **E-commerce Features** - Shopping cart, orders, payments, delivery tracking
- **Discount System** - Promo codes and discount management
- **Address Management** - Shipping and billing addresses
- **Audit Logging** - Track order changes
- **OpenAPI Documentation** - Interactive API docs with Swagger UI

---

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Java**: 17
- **Build Tool**: Maven 3.6+
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security + JWT
- **API Docs**: SpringDoc OpenAPI (Swagger)
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Logging**: SLF4J with Logback

---

## ✅ Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+** (or use Docker)
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/azlicn/pet-store.git
cd pet-store/pet-store-api
```

### 2. Configure Database

**Option A: Using Docker (Recommended)**
```bash
cd ../docker
docker-compose up -d mysql
```

**Option B: Manual MySQL Setup**
```bash
mysql -u root -p
CREATE DATABASE petstore_db;
```

### 3. Configure Application Properties

Copy and update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/petstore_db
spring.datasource.username=root
spring.datasource.password=yourpassword

# JWT Configuration
app.jwt.secret=your-secret-key-at-least-256-bits-long
app.jwt.expirationMs=86400000
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

---

## 📁 Project Structure

```
pet-store-api/
├── src/main/java/com/petstore/
│   ├── PetStoreApplication.java        # Main application class
│   ├── config/                         # Configuration classes
│   │   ├── DataInitializer.java        # Initial data setup
│   │   ├── OpenApiConfig.java          # Swagger configuration
│   │   ├── SecurityConfig.java         # Spring Security setup
│   │   └── OrderGeneratorConfig.java   # Order number generator config
│   ├── controller/                     # REST controllers
│   │   ├── AddressController.java
│   │   ├── AuthController.java
│   │   ├── CategoryController.java
│   │   ├── DiscountController.java
│   │   ├── PetController.java
│   │   ├── StoreController.java
│   │   └── UserController.java
│   ├── dto/                            # Data Transfer Objects
│   │   ├── LoginRequest.java
│   │   ├── PaymentOrderRequest.java
│   │   ├── PetPageResponse.java
│   │   ├── SignUpRequest.java
│   │   └── UserUpdateRequest.java
│   ├── enums/                          # Enumeration types
│   │   ├── OrderStatus.java
│   │   ├── PaymentStatus.java
│   │   ├── PaymentType.java
│   │   ├── PetStatus.java
│   │   └── DeliveryStatus.java
│   ├── exception/                      # Custom exceptions
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ErrorResponse.java
│   │   └── [30+ custom exceptions]
│   ├── generator/                      # Order number generators
│   │   ├── OrderNumberGenerator.java
│   │   ├── UUIDOrderNumberGenerator.java
│   │   ├── SequentialOrderNumberGenerator.java
│   │   └── TimeBasedOrderNumberGenerator.java
│   ├── model/                          # JPA entities
│   │   ├── Address.java
│   │   ├── Cart.java
│   │   ├── CartItem.java
│   │   ├── Category.java
│   │   ├── Delivery.java
│   │   ├── Discount.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Payment.java
│   │   ├── Pet.java
│   │   └── User.java
│   ├── repository/                     # Spring Data repositories
│   │   ├── AddressRepository.java
│   │   ├── CartRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── DiscountRepository.java
│   │   ├── OrderRepository.java
│   │   ├── PaymentRepository.java
│   │   ├── PetRepository.java
│   │   └── UserRepository.java
│   ├── security/                       # Security components
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtTokenProvider.java
│   │   └── UserPrincipal.java
│   ├── service/                        # Business logic
│   │   ├── AddressService.java
│   │   ├── CartService.java
│   │   ├── CategoryService.java
│   │   ├── DiscountService.java
│   │   ├── OrderService.java
│   │   ├── PetService.java
│   │   └── UserService.java
│   └── strategy/                       # Strategy pattern implementations
│       ├── PaymentStrategy.java
│       ├── EWalletStrategy.java
│       └── [payment strategy implementations]
├── src/main/resources/
│   ├── application.properties          # Main configuration
│   ├── application-docker.properties   # Docker configuration
│   └── logback-spring.xml             # Logging configuration
├── src/test/                           # Test classes
│   ├── java/com/petstore/
│   │   ├── controller/                 # Controller tests
│   │   ├── service/                    # Service tests
│   │   └── repository/                 # Repository tests
│   └── resources/
│       └── application-test.properties # Test configuration
├── logs/                               # Application logs
├── Dockerfile                          # Docker build file
└── pom.xml                            # Maven dependencies
```

---

## ⚙️ Configuration

### Application Properties

#### Database Configuration
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/petstore_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

#### JWT Configuration
```properties
app.jwt.secret=your-secret-key-at-least-256-bits-long
app.jwt.expirationMs=86400000
```

#### Order Number Generator
```properties
# Options: uuid, sequential, timeBased
app.order.generator.type=uuid
```

#### Server Configuration
```properties
server.port=8080
server.servlet.context-path=/api
```

### Environment Profiles

- **default** - Local development
- **docker** - Docker containerized environment
- **test** - Testing environment

Activate profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

---

## 🏃 Running the Application

### Development Mode
```bash
mvn spring-boot:run
```

### Production Mode
```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/pet-store-api-0.0.1-SNAPSHOT.jar
```

### With Docker
```bash
# Build image
docker build -t pet-store-api .

# Run container
docker run -p 8080:8080 pet-store-api
```

### With VS Code
Use the configured tasks:
1. Press `Ctrl+Shift+P` (Cmd+Shift+P on Mac)
2. Select "Tasks: Run Task"
3. Choose "Start Backend"

---

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=PetServiceTest
```

### Run with Coverage
```bash
mvn test jacoco:report
```

### Test Coverage Report
View coverage report at: `target/site/jacoco/index.html`

### Test Categories

- **Unit Tests** - Service and utility classes
- **Integration Tests** - Repository and controller tests
- **Security Tests** - Authentication and authorization

---

## 📖 API Documentation

### Swagger UI
Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Specification
View raw OpenAPI spec at:
```
http://localhost:8080/v3/api-docs
```

### Main Endpoints

#### Authentication
- `POST /api/auth/signup` - Register new user
- `POST /api/auth/login` - User login

#### Pets
- `GET /api/pets` - Get all pets (paginated)
- `GET /api/pets/{id}` - Get pet by ID
- `POST /api/pets` - Create pet (ADMIN)
- `PUT /api/pets/{id}` - Update pet (ADMIN/Owner)
- `DELETE /api/pets/{id}` - Delete pet (ADMIN/Owner)

#### Orders
- `POST /api/stores/checkout` - Checkout cart
- `POST /api/stores/order/{orderId}/pay` - Make payment
- `GET /api/stores/orders` - Get orders
- `DELETE /api/stores/order/{orderId}` - Cancel order

#### Admin
- `GET /api/categories` - Manage categories
- `GET /api/discounts` - Manage discounts
- `GET /api/users` - Manage users

---

## 🎨 Design Patterns

The backend implements several design patterns for maintainability and scalability:

### 1. Strategy Pattern
- **Order Number Generator**: Three interchangeable implementations (UUID, Sequential, Time-based)
- **Payment Processing**: Multiple payment methods (Credit Card, Debit Card, E-Wallet, PayPal)

### 2. Factory Pattern
- **Payment Strategy Factory**: Selects appropriate payment strategy
- **E-Wallet Strategy Factory**: Handles different e-wallet types

### 3. Repository Pattern
- Spring Data JPA repositories abstract data access

### 4. Service Layer Pattern
- Business logic encapsulated in service classes

### 5. MVC Pattern
- Controllers handle requests, services process logic, repositories access data

For detailed design pattern documentation, see [Design Patterns](../docs/design-patterns.md).

---

## 💾 Database

### Entity Relationship

Main entities:
- **User** - System users (customers and admins)
- **Pet** - Pet listings
- **Category** - Pet categories
- **Cart** - Shopping carts
- **Order** - Customer orders
- **Payment** - Payment records
- **Delivery** - Delivery tracking
- **Address** - Shipping/billing addresses
- **Discount** - Promotional codes

### Database Schema
See complete ER diagram in [Architecture Documentation](../docs/architecture.md).

### Migrations
We use JPA's `ddl-auto=update` for development. For production, consider using Flyway or Liquibase.

---

## 🔐 Security

### Authentication
- **JWT (JSON Web Tokens)** for stateless authentication
- Tokens expire after 24 hours (configurable)
- Refresh token mechanism (planned)

### Authorization
- **Role-Based Access Control (RBAC)**
  - `USER` - Standard user permissions
  - `ADMIN` - Full administrative access

### Password Security
- Passwords encrypted with BCrypt
- Minimum password requirements enforced
- Password change functionality

### API Security
- All endpoints except `/auth/**` require authentication
- JWT token in Authorization header: `Bearer <token>`
- CORS configured for frontend origin

### Security Best Practices
- SQL injection prevention via prepared statements
- XSS protection with Spring Security headers
- CSRF protection (for non-API endpoints)
- Secure headers configuration

---

## 📝 Logging

Logs are written to:
- **Console** - All log levels in development
- **File** - `logs/pet-store.log` with daily rotation

Log levels:
- `ERROR` - System errors
- `WARN` - Warnings and unexpected situations
- `INFO` - General information
- `DEBUG` - Detailed debugging information

Configure in `logback-spring.xml`.

---

## 🚀 Deployment

### Docker
```bash
docker build -t pet-store-api .
docker run -p 8080:8080 pet-store-api
```

### Docker Compose
```bash
cd ../docker
docker-compose up -d
```

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
- [Design Patterns](../docs/design-patterns.md)

---

**Made with ❤️ by the Pawfect Store Team**
