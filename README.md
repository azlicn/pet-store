# 🐾 Pawfect Store - 3-Tier Pet Store Application

A modern, full-stack pet store application built with Angular 17 and Spring Boot 3.2.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)](https://spring.io/projects/spring-boot) [![Angular](https://img.shields.io/badge/Angular-17-red)](https://angular.io/) [![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)


<p align="center">
  <img src="docs/assets/pawfect.png" alt="Pawfect Store" width="1200"/>
</p>

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 Overview

Pawfect Store is a full-stack pet store management application, featuring:

- **Modern Frontend**: Angular 17 with standalone components and Angular Material UI
- **Robust Backend**: Spring Boot 3.2 REST API with JWT authentication
- **Reliable Database**: MySQL 8.0 with JPA/Hibernate ORM
- **Containerized Deployment**: Docker and Docker Compose support
- **CI/CD Ready**: GitHub Actions workflow configuration

---

## ✨ Features

### Core Functionality
- 🐶 **Pet Management**: Browse, add, update, and delete pet listings
- 🛒 **Shopping Cart**: Add pets to cart and manage orders
- 💳 **Payment Processing**: Mock payment system with multiple payment types
- 📦 **Order Tracking**: View order history and delivery status
- 🏷️ **Category Management**: Organize pets by categories
- 💰 **Discount System**: Apply discount codes at checkout
- 📍 **Address Management**: Manage shipping and billing addresses

### Advanced Features
- 🔐 **JWT Authentication**: Secure token-based authentication
- 👥 **Role-Based Access**: User and Admin roles with different permissions
- 🔍 **Search & Filter**: Advanced pet search and filtering
- 📱 **Responsive Design**: Mobile-friendly Angular Material UI
- 📊 **API Documentation**: Interactive Swagger/OpenAPI docs
- 🐳 **Docker Support**: Containerized deployment

---

## 🛠️ Tech Stack

### Frontend
- **Framework**: Angular 17 (Standalone Components)
- **UI Library**: Angular Material
- **State Management**: Services with RxJS
- **HTTP Client**: Angular HttpClient
- **Routing**: Angular Router with Guards

### Backend
- **Framework**: Spring Boot 3.2
- **Security**: Spring Security + JWT
- **ORM**: JPA/Hibernate
- **API Docs**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven

### Database
- **RDBMS**: MySQL 8.0
- **Migration**: JPA Auto DDL

### DevOps
- **Containerization**: Docker & Docker Compose
- **CI/CD**: GitHub Actions
- **Version Control**: Git

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Maven 3.6+
- MySQL 8.0+ (or use Docker)

### Installation

```bash
# Clone the repository
git clone https://github.com/azlicn/pet-store.git
cd pet-store

# Start with Docker (Recommended)
cd docker
docker-compose up -d

# Or setup manually
# See docs/setup.md for detailed instructions
```

### Access the Application

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080/api
- **API Docs**: http://localhost:8080/swagger-ui.html

### Default Admin Credentials

```
Email: admin@pawfect.com
Password: admin123
```

---

## 📁 Project Structure

```
pet-store/
├── pet-store-api/          # Spring Boot backend
├── pet-store-frontend/     # Angular frontend
├── docker/                 # Docker configuration
├── docs/                   # Documentation
├── .vscode/               # VS Code tasks
└── README.md              # This file
```

---

## 📚 Documentation

Comprehensive documentation is available in the `docs/` directory:

- **[Architecture](docs/architecture.md)** - System architecture, component design, database schema
- **[Setup Guide](docs/setup.md)** - Detailed setup instructions for local and Docker environments
- **[API Documentation](docs/api.md)** - Complete REST API reference with flow diagrams
- **[Design Patterns](docs/design-patterns.md)** - Design patterns used in the project
- **[Deployment Guide](docs/deployment.md)** - Docker deployment and CI/CD pipeline
- **[Troubleshooting](docs/troubleshooting.md)** - Common issues and solutions

### Component-Specific Docs
- **[Backend Documentation](pet-store-api/README.md)** - Spring Boot backend details
- **[Frontend Documentation](pet-store-frontend/README.md)** - Angular frontend details

---

## 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) for details on:

- Code of conduct
- Development workflow
- Coding standards
- Pull request process

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🌟 Future Enhancements

- [ ] Real payment gateway integration (Stripe/PayPal)
- [ ] Email notifications for orders
- [ ] Advanced search with Elasticsearch
- [ ] Pet adoption/rehoming feature
- [ ] Mobile app (React Native/Flutter)
- [ ] Admin analytics dashboard
- [ ] Review and rating system
- [ ] Wishlist functionality
- [ ] Database migration with Flyway or Liquibase (currently using JPA Auto DDL)
- [ ] Microservices Architecture
- [ ] Event-Driven Communication
- [ ] Monitoring and Observability

---

**Made with ❤️ by the Pawfect Store Team**
