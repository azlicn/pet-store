# 🚀 Setup Guide

> Complete setup instructions for local development and production deployment.

---

## 📋 Table of Contents

- [Quick Start](#quick-start)
  - [Prerequisites](#prerequisites)
  - [1. Clone and Setup](#1-clone-and-setup)
  - [2. Database Setup](#2-database-setup)
  - [3. Run the Application](#3-run-the-application)
  - [4. Access the Application](#4-access-the-application)
  - [5. Initial Data Setup](#5-initial-data-setup)
- [Configuration](#configuration)
  - [Backend Configuration](#backend-configuration-applicationproperties)
  - [Frontend Configuration](#frontend-configuration)
- [Local Environment Configuration](#local-environment-configuration)
  - [Setup Instructions](#setup-instructions)
- [Docker Environment Configuration](#docker-environment-configuration)
  - [Docker Environment Variables](#docker-environment-variables)
  - [Environment Files](#environment-files)
  - [Variable Precedence](#variable-precedence)
  - [Profile-Specific Behavior](#profile-specific-behavior)

---

## Quick Start

### Prerequisites
- **Java 17+** (OpenJDK recommended)
- **Node.js 18+** and npm
- **Maven 3.6+**
- **MySQL 8.0+** (or Docker for containerized database)

### 1. Clone and Setup
```bash
# Navigate to the project directory
cd pet-store

# Install frontend dependencies
cd pet-store-frontend
npm install

# Build frontend
npm run build

# Build backend
cd ../pet-store-api
mvn clean package
```

### 2. Database Setup
```bash
# Option A: Local MySQL
mysql -u root -p
CREATE DATABASE petstore_db;

# Option B: Using Docker
cd docker
docker-compose up -d mysql
```

### 3. Run the Application

#### Using VS Code Tasks (Recommended)
1. Open the project in VS Code
2. Press `Ctrl+Shift+P` (Cmd+Shift+P on Mac)
3. Type "Tasks: Run Task"
4. Select "Start Backend" to launch the Spring Boot server
5. Select "Start Frontend" to launch the Angular development server

#### Manual Start
```bash
# Terminal 1: Start Backend
cd pet-store-api
mvn spring-boot:run

# Terminal 2: Start Frontend
cd pet-store-frontend
npm start
```

### 4. Access the Application
- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080/api
- **API Documentation**: http://localhost:8080/swagger-ui.html

### 5. Initial Data Setup

The application automatically creates initial data on first startup through the `DataInitializer` component:

#### Default Admin User
A default administrator account is created for demo purposes:
- **Email**: `admin@pawfect.com`
- **Password**: `admin123`
- **Roles**: ADMIN
- **Access**: Full system administration capabilities

#### Default Categories
The following pet categories are automatically created:
- **Dogs** - Canine companions
- **Cats** - Feline friends  
- **Birds** - Feathered pets
- **Fish** - Aquatic animals
- **Reptiles** - Cold-blooded pets
- **Small Pets** - Rabbits, hamsters, etc.

#### Sample Pets
Demo pets are created in each category, including:

**Dogs:**
- Golden Retriever – Sunny Buddy ($1,200)
- German Shepherd – Brave Rex ($1,500)  
- Labrador – Happy Bella ($1,000)

**Cats:**
- Persian Cat – Royal Luna ($800)
- Siamese Cat – Mister Milo ($600)

**Birds:**
- Canary – Golden Song ($150)
- Parrot – Talking Rio ($500)

**Fish:**
- Goldfish – Golden Bubbles ($25)
- Betta Fish – Blue Sapphire ($15)

All sample pets include multiple high-quality photos and relevant tags for demonstration purposes.

> **Note**: Initial data is only created when the database is empty. Existing data will not be overwritten on subsequent application starts.

---

## Configuration

### Backend Configuration (application.properties)
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/petstore_db
spring.datasource.username=your_datasource_user
spring.datasource.password=your_datasource_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
app.jwt.secret=your_secret_key
app.jwt.expiration-ms=86400000
```

### Frontend Configuration
The Angular app is configured to connect to the backend at `http://localhost:8080/api`.


---

## Local Environment Configuration

This section explains how to configure environment variables for local development to securely manage sensitive configuration like database passwords and JWT secrets.

### Setup Instructions

#### 1. Copy Environment Template
```bash
# In the pet-store-api directory
cd pet-store-api
cp .env.example .env
```

#### 2. Configure Your Environment
Edit the `.env` file with your actual values:

```bash
# Database Configuration
DB_PASSWORD=your_mysql_password
DB_USERNAME=root
DB_URL=jdbc:mysql://localhost:3306/petstore_db?createDatabaseIfNotExist=true&serverTimezone=UTC

# JWT Configuration - Generate a secure 256-bit secret
JWT_SECRET=your_super_secure_jwt_secret_key_here_at_least_256_bits_long
JWT_EXPIRATION=86400000

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:4200

# Server Configuration
SERVER_PORT=8080
```

#### 3. Generate Secure JWT Secret
For production, generate a secure JWT secret:

```bash
# Using openssl (recommended)
openssl rand -base64 64

# Using Node.js
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"

# Using Python
python3 -c "import secrets; print(secrets.token_urlsafe(64))"
```

### How It Works

1. **Environment Loading**: The `EnvironmentConfig` class loads variables from `.env` at startup
2. **Spring Integration**: Variables are available to Spring's `${VARIABLE_NAME:default}` syntax
3. **Priority Order**: Environment variables → .env file → default values
4. **Security**: `.env` file is gitignored and won't be committed

### Local Environment Variables Reference

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_PASSWORD` | MySQL database password | empty | No |
| `DB_USERNAME` | MySQL database username | root | No |
| `DB_URL` | JDBC connection URL | localhost:3306/petstore_db | No |
| `JWT_SECRET` | Secret key for JWT signing | default (insecure) | **Yes for production** |
| `JWT_EXPIRATION` | JWT token expiration time (ms) | 86400000 (24h) | No |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | http://localhost:4200 | No |
| `SERVER_PORT` | Server port | 8080 | No |

### Environment-Specific Configurations

#### Development
```bash
# .env
DB_PASSWORD=your_local_mysql_password
JWT_SECRET=your_development_jwt_secret_here
CORS_ALLOWED_ORIGINS=http://localhost:4200
```

#### Staging
```bash
# .env
DB_PASSWORD=your_staging_db_password_here
JWT_SECRET=your_staging_jwt_secret_256_bits_here
CORS_ALLOWED_ORIGINS=https://staging.petstore.com
```

#### Production
```bash
# Set via environment variables or container secrets
export DB_PASSWORD="your_production_secure_password"
export JWT_SECRET="your_production_jwt_secret_256_bits"
export CORS_ALLOWED_ORIGINS="https://petstore.com"
```

### Running the Application Locally

#### With .env file (Development)
```bash
# Just run normally - .env is loaded automatically
cd pet-store-api
mvn spring-boot:run
```

#### With Environment Variables (Production)
```bash
# Set environment variables and run
export DB_PASSWORD="secure_password"
export JWT_SECRET="super_secure_jwt_secret"
mvn spring-boot:run
```

### Local Environment Security Best Practices

1. **Never commit `.env` files** - they're gitignored
2. **Use strong JWT secrets** - minimum 256 bits (32+ characters)
3. **Rotate secrets regularly** - especially JWT secrets
4. **Use different secrets per environment**
5. **Consider external secret management** for production (Vault, AWS Secrets Manager)

### Environment Troubleshooting

#### Check if variables are loaded:
```bash
# Check application logs for:
✅ Environment variables loaded from .env file
# or
⚠️  No .env file found or error loading it
```

#### Verify configuration at runtime:
Visit the actuator endpoint (admin access required):
```
http://localhost:8080/actuator/env
```

#### Common Environment Issues:
1. **File not found**: Ensure `.env` is in the `pet-store-api` directory
2. **Variables not loaded**: Check file format (KEY=VALUE, no spaces around =)
3. **Still using defaults**: Environment variables override .env values

#### Example Environment Files

##### .env.example (Template)
```bash
DB_PASSWORD=your_mysql_password_here
JWT_SECRET=your_very_secure_jwt_secret_key_here_minimum_256_bits
```

##### .env (Your actual file - gitignored)
```bash
DB_PASSWORD=your_actual_secure_password
JWT_SECRET=your_actual_generated_jwt_secret_256_bits
```

> **Note**: The `.env` file is automatically loaded, and you can override any value with actual environment variables!


---

## Docker Environment Configuration

The application supports environment-specific configuration through Spring profiles and Angular environments.

### Docker Environment Variables

#### Database Configuration
| Variable | Description | Default | Used in |
|----------|-------------|---------|---------|
| `MYSQL_ROOT_PASSWORD` | MySQL root password | your_actual_secure_password | MySQL container & Backend |
| `MYSQL_DATABASE` | Database name | petstore_db | MySQL container & Backend |

#### Backend Configuration (Docker Profile)
| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DOCKER_JWT_SECRET` | JWT signing secret for Docker | default (insecure) | **Yes for production** |
| `DOCKER_JWT_EXPIRATION` | JWT expiration time (ms) | 86400000 (24h) | No |
| `DOCKER_CORS_ALLOWED_ORIGINS` | CORS origins for Docker | http://localhost | No |

#### Frontend Configuration
| Variable | Description | Default |
|----------|-------------|---------|
| `API_URL` | Backend API URL for frontend | http://petstore-backend:8080/api |

### Environment Files

#### Development Docker Environment (`.env`)
```bash
# Database
MYSQL_ROOT_PASSWORD=your_mysql_password_here
MYSQL_DATABASE=petstore_db

# Backend (Docker-specific)
DOCKER_JWT_SECRET=your_secure_jwt_secret_here_minimum_256_bits
DOCKER_JWT_EXPIRATION=86400000
DOCKER_CORS_ALLOWED_ORIGINS=http://localhost

# Frontend
API_URL=http://petstore-backend:8080/api
```

#### Production Docker Environment
```bash
# Database
MYSQL_ROOT_PASSWORD=your_production_secure_password_here
MYSQL_DATABASE=petstore_db

# Backend (Docker-specific) - GENERATE SECURE VALUES
DOCKER_JWT_SECRET=your_production_jwt_secret_256_bits_here
DOCKER_JWT_EXPIRATION=3600000
DOCKER_CORS_ALLOWED_ORIGINS=https://your-domain.com

# Frontend
API_URL=https://your-api-domain.com/api
```

### Environment Variable Flow

```
Docker .env file → Docker Compose → Container Environment → application-docker.properties
```

1. **Docker `.env`**: Contains all environment-specific values
2. **Docker Compose**: Reads `.env` and passes variables to containers
3. **Container**: Receives environment variables
4. **Spring Boot**: Loads `application-docker.properties` with variable substitution

### Docker Usage Examples

#### Development Setup
```bash
cd docker
cp .env.example .env
# Edit .env with your values
docker-compose up -d
```

#### Production Setup
```bash
cd docker
# Create secure .env file
echo "MYSQL_ROOT_PASSWORD=your_secure_password" > .env
echo "DOCKER_JWT_SECRET=$(openssl rand -base64 64)" >> .env
echo "DOCKER_CORS_ALLOWED_ORIGINS=https://your-domain.com" >> .env
docker-compose up -d
```

#### Check Configuration
```bash
# View backend container environment
docker exec petstore-backend env | grep DOCKER_

# View database connection
docker exec petstore-backend env | grep DB_
```

### Variable Precedence

1. **Container environment variables** (highest priority)
2. **Docker Compose environment section**
3. **Docker .env file**
4. **application-docker.properties defaults** (lowest priority)

### Profile-Specific Behavior

The application uses different variable names for different profiles:

#### Local Development (`application.properties`)
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS`

#### Docker Environment (`application-docker.properties`)
- `DOCKER_DB_PASSWORD`
- `DOCKER_JWT_SECRET`
- `DOCKER_CORS_ALLOWED_ORIGINS`

This separation ensures no conflicts between local and Docker configurations.

