# Wasla - Logistics Marketplace Backend

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A modern logistics marketplace platform that connects clients with independent truck drivers through a competitive bidding system. Built with Spring Boot 3, PostgreSQL, and real-time WebSocket communication.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Configuration](#configuration)
- [Development](#development)
- [Deployment](#deployment)
- [Contributing](#contributing)

## 🎯 Overview

Wasla is a two-sided marketplace platform that revolutionizes the moving/logistics industry by:

- **Eliminating middlemen**: Direct connection between clients and drivers
- **Price transparency**: Competitive bidding ensures fair market pricing
- **Real-time matching**: Location-based job discovery for drivers
- **Trust building**: Rating system ensures quality service

### How It Works

1. **Clients** post move requests with pickup/dropoff locations
2. **Nearby drivers** receive notifications and submit price bids
3. **Clients** review bids and accept the best offer
4. **System** tracks job progress from confirmation to completion
5. **Clients** rate drivers after job completion

## ✨ Features

### Core Functionality

- ✅ **Authentication & Authorization**
  - Email/password registration and login
  - JWT access tokens with refresh token rotation
  - Role-based access control (CLIENT, DRIVER, ADMIN)
  - BCrypt password hashing

- ✅ **Job Management**
  - Create jobs with pickup/dropoff locations
  - Automatic job expiry (30 minutes)
  - Job lifecycle tracking (OPEN → BIDDING → CONFIRMED → IN_PROGRESS → COMPLETED)
  - Cargo photo upload support

- ✅ **Bidding System**
  - Drivers submit competitive price bids
  - Multiple bids per job
  - Automatic bid withdrawal on acceptance
  - Bid history tracking

- ✅ **Real-time Location Tracking**
  - WebSocket-based GPS updates
  - Live driver location broadcasting
  - Client-side map integration ready

- ✅ **Rating System**
  - Post-job driver ratings (1-5 stars)
  - Automatic average rating calculation
  - Rating comments and feedback

- ✅ **Profile Management**
  - Client profile updates
  - Driver profile with vehicle information
  - Online/offline status tracking
  - FCM token management for push notifications

### Technical Features

- 🔐 Secure JWT authentication with refresh token rotation
- 🗄️ PostgreSQL with PostGIS extension (spatial queries ready)
- 🚀 Redis caching layer
- 📊 Database migrations with Flyway
- 📝 Comprehensive API documentation with Swagger/OpenAPI
- 🔄 Real-time communication via WebSocket (STOMP)
- 🏗️ Clean layered architecture
- 🗺️ MapStruct for entity-DTO mapping
- ⏰ Scheduled tasks for job expiry
- 🐳 Docker support with multi-stage builds

## 🛠️ Technology Stack

### Core Framework
- **Java 17** - Programming language
- **Spring Boot 3.3.3** - Application framework
- **Maven** - Build tool and dependency management

### Spring Ecosystem
- Spring Web - REST API development
- Spring Data JPA - Database abstraction
- Spring Security - Authentication and authorization
- Spring WebSocket - Real-time communication
- Spring Validation - Request validation
- Spring Data Redis - Caching layer

### Database & Persistence
- **PostgreSQL 16** - Primary database
- **PostGIS 3.4** - Geospatial extension
- **Flyway** - Database migration management
- **Hibernate** - JPA implementation
- **HikariCP** - Connection pooling

### Security & Authentication
- **JJWT 0.13.0** - JWT token generation/validation
- **BCrypt** - Password hashing

### Development Tools
- **Lombok** - Boilerplate code reduction
- **MapStruct 1.5.5** - Entity-DTO mapping
- **SpringDoc OpenAPI 2.6.0** - API documentation
- **Spring DevTools** - Hot reload

### Infrastructure
- **Docker & Docker Compose** - Containerization
- **Redis 7** - Caching and session storage
- **pgAdmin 4** - Database management UI

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     CLIENT APPLICATIONS                      │
│              (Mobile Apps, Web Frontend)                     │
└────────────────────────┬────────────────────────────────────┘
                         │ REST API / WebSocket
┌────────────────────────┴────────────────────────────────────┐
│                   PRESENTATION LAYER                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Controllers  │  │  DTOs        │  │  Mappers     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────────┐
│                    BUSINESS LAYER                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Services   │  │   Mappers    │  │  Schedulers  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────────┐
│                   PERSISTENCE LAYER                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Repositories │  │   Entities   │  │  Flyway      │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────────┐
│                   INFRASTRUCTURE                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  PostgreSQL  │  │    Redis     │  │   Firebase   │      │
│  │  (PostGIS)   │  │   (Cache)    │  │    (FCM)     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

### Package Structure

```
com.example.wasla/
├── auth/               # Authentication & authorization
├── common/            # Shared utilities (DTOs, exceptions)
├── config/           # Spring configuration classes
├── job/              # Job and bidding domain
├── location/         # Real-time location tracking
├── notification/     # Push notification service
├── rating/          # Driver rating system
└── user/            # User management (client & driver)
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose (for infrastructure)
- PostgreSQL 16 (if running locally without Docker)
- Redis 7 (if running locally without Docker)

### Quick Start with Docker

1. **Clone the repository**
```bash
git clone https://github.com/alaa-333/Wasla.git
cd Wasla/wasla-backend/wasla
```

2. **Start infrastructure (PostgreSQL, Redis, pgAdmin)**
```bash
docker-compose -f docker-compose-dev.yml up -d
```

3. **Run the application**
```bash
./mvnw spring-boot:run
```

Or on Windows:
```bash
start.bat
```

4. **Access the application**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- pgAdmin: http://localhost:5050 (admin@wasla.com / admin)

### Full Stack Deployment

To run everything in Docker (including the application):

```bash
docker-compose up -d
```

This will start:
- PostgreSQL (port 5432)
- Redis (port 6379)
- Wasla Backend (port 8080)

## 📚 API Documentation

### Interactive Documentation

Once the application is running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Key Endpoints

#### Authentication
```
POST /api/v1/auth/register/client    - Register as client
POST /api/v1/auth/register/driver    - Register as driver
POST /api/v1/auth/login               - Login
POST /api/v1/auth/refresh             - Refresh access token
POST /api/v1/auth/logout              - Logout
```

#### Jobs
```
POST   /api/v1/jobs                   - Create job (CLIENT)
GET    /api/v1/jobs/{id}              - Get job details
GET    /api/v1/jobs/my                - Get my jobs (paginated)
GET    /api/v1/jobs/nearby            - Find nearby jobs (DRIVER)
PATCH  /api/v1/jobs/{id}/status       - Update job status (DRIVER)
```

#### Bids
```
POST   /api/v1/bids                   - Submit bid (DRIVER)
GET    /api/v1/bids/job/{jobId}       - Get bids for job (CLIENT)
PATCH  /api/v1/bids/{id}/accept       - Accept bid (CLIENT)
```

#### Ratings
```
POST   /api/v1/ratings                - Rate driver (CLIENT)
GET    /api/v1/ratings/driver/{id}    - Get driver ratings
```

#### Profile Management
```
GET    /api/v1/clients/me             - Get my profile (CLIENT)
PUT    /api/v1/clients/me/profile     - Update profile (CLIENT)
GET    /api/v1/drivers/me             - Get my profile (DRIVER)
PUT    /api/v1/drivers/me/profile     - Update profile (DRIVER)
PUT    /api/v1/drivers/me/status      - Update availability (DRIVER)
PUT    /api/v1/drivers/me/location    - Update GPS location (DRIVER)
```

### WebSocket Endpoints

```
CONNECT /ws                           - WebSocket connection
SEND    /app/driver.location          - Send GPS update
SUBSCRIBE /topic/job/{jobId}/location - Receive location updates
```

## 🗄️ Database Schema

### Main Entities

- **clients** - Client profiles and authentication
- **drivers** - Driver profiles, vehicle info, and GPS coordinates
- **jobs** - Move requests with pickup/dropoff locations
- **bids** - Driver price quotes on jobs
- **ratings** - Client feedback on completed jobs
- **refresh_tokens** - Persistent token storage for session management

### Migrations

Database schema is managed with Flyway:
- **V1__init_schema.sql** - Initial schema with BIGSERIAL IDs
- **V2__refactor_to_separate_entities.sql** - Refactored to UUID-based separate entities

Migrations run automatically on application startup.

## ⚙️ Configuration

### Environment Variables

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/wasla
SPRING_DATASOURCE_USERNAME=wasla
SPRING_DATASOURCE_PASSWORD=secret

# Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

# JWT
JWT_SECRET=your-secret-key-must-be-at-least-256-bits
```

### Application Profiles

- **dev** - Development profile (verbose logging, dev database)
- **prod** - Production profile (optimized settings)
- **test** - Test profile (in-memory database)

Activate profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Database Configuration

Default connection pool settings (HikariCP):
```yaml
hikari:
  maximum-pool-size: 10
  minimum-idle: 5
  connection-timeout: 30000
  idle-timeout: 600000
  max-lifetime: 1800000
```

## 💻 Development

### Build the Project

```bash
./mvnw clean install
```

### Run Tests

```bash
./mvnw test
```

### Run with Hot Reload

```bash
./mvnw spring-boot:run
```

Spring DevTools is included for automatic restart on code changes.

### Code Quality

The project follows:
- Clean Architecture principles
- SOLID principles
- DRY (Don't Repeat Yourself)
- Separation of concerns

Recent improvements:
- ✅ Fixed syntax errors (column names, imports, annotations)
- ✅ Removed redundant code (duplicate logic, unnecessary variables)
- ✅ Improved null safety (proper Optional handling)
- ✅ Enhanced security (JWT logging levels)

### Database Management

Access pgAdmin at http://localhost:5050:
- Email: admin@wasla.com
- Password: admin

Connect to PostgreSQL:
- Host: postgres (or localhost if running locally)
- Port: 5433 (dev) or 5432 (prod)
- Database: wasla
- Username: wasla
- Password: secret

## 🐳 Deployment

### Docker Build

Build the application image:
```bash
docker build -t wasla-backend:latest .
```

Or use multi-stage build for optimized image:
```bash
docker build -f Dockerfile.multistage -t wasla-backend:latest .
```

### Docker Compose Deployment

Full stack deployment:
```bash
docker-compose up -d
```

Development infrastructure only:
```bash
docker-compose -f docker-compose-dev.yml up -d
```

### Health Checks

The application includes health checks for:
- PostgreSQL (automatic retry on startup)
- Redis (automatic retry on startup)

## 📝 Recent Changes

### Version 3.1 (Latest)
- ✅ Fixed syntax errors in BaseEntity, UserRepository, PaginationRequest
- ✅ Removed redundant code in AuthService, JwtService, OrderMapper
- ✅ Improved null safety in BeanConfiguration
- ✅ Enhanced security (JWT logging)
- ✅ Restructured to feature-based package architecture
- ✅ Added Docker support with multi-stage builds
- ✅ Implemented job bidding system with scheduler
- ✅ Added rating and notification services
- ✅ Separated Client and Driver entities
- ✅ Added Flyway database migrations

### Version 3.0
- ✅ Renamed project from "MoveMate" to "Wasla"
- ✅ Refactored to layered architecture
- ✅ Replaced manual mappers with MapStruct
- ✅ Implemented refresh token rotation
- ✅ Added profile management endpoints
- ✅ Added Docker Compose configurations

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Commit Convention

Follow conventional commits:
- `feat:` - New feature
- `fix:` - Bug fix
- `refactor:` - Code refactoring
- `docs:` - Documentation changes
- `test:` - Test additions/changes
- `chore:` - Maintenance tasks

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Alaa** - Initial work - [alaa-333](https://github.com/alaa-333)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- PostgreSQL and PostGIS communities
- MapStruct for compile-time mapping
- All contributors and supporters

## 📞 Support

For support, email support@wasla.com or open an issue on GitHub.

---

**Built with ❤️ using Spring Boot**
