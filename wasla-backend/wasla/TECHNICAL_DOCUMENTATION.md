# Wasla Backend - Technical Documentation

**Version 3.1** | Last Updated: March 27, 2026

## Recent Changes (v3.1)

This version includes clarifications and corrections based on actual implementation:

**Documentation Accuracy:**
- ✅ Clarified PostGIS status: Extension available but spatial queries not yet implemented
- ✅ Updated notification service: Stub implementation, FCM integration pending
- ✅ Documented actual nearby jobs behavior: Returns all open jobs (no distance filtering yet)
- ✅ Added database migration details: V1 (init) and V2 (UUID refactor)

**Docker & Infrastructure:**
- ✅ Documented three deployment options: dev-only, full-stack, Windows script
- ✅ Clarified port configurations: 5433 for dev, 5432 for prod
- ✅ Added health checks for PostgreSQL and Redis
- ✅ Documented multi-stage Dockerfile for optimized production builds
- ✅ Updated base images: Eclipse Temurin 17, PostGIS 16-3.4, Redis 7 Alpine

**Configuration:**
- ✅ Added HikariCP connection pool settings
- ✅ Documented profile-specific configurations (dev, prod, test)
- ✅ Clarified environment variable requirements per deployment mode

---

## Recent Changes (v3.0)

This version includes significant updates to project naming, architecture, Docker infrastructure, and code quality:

**Project Rename:**
- ✅ Renamed project from "MoveMate" to "Wasla" across all configurations
- ✅ Updated package namespace to `com.example.wasla`
- ✅ Updated Docker container names, database name, and all references

**Architecture & Code Quality:**
- ✅ Refactored to layered architecture with clear separation of concerns
- ✅ Replaced manual mappers with MapStruct for entity-DTO conversions
- ✅ Added Lombok-MapStruct binding for seamless integration
- ✅ Custom exception hierarchy with `WaslaAppException` base class

**Authentication & Security:**
- ✅ Email & Password based registration and login
- ✅ Implemented refresh token rotation for secure long-lived sessions
- ✅ Included secure `/api/v1/auth/refresh` and `/api/v1/auth/logout` endpoints with token invalidation
- ✅ BCrypt password hashing

**Docker & Infrastructure:**
- ✅ Added `docker-compose-dev.yml` for local dev infrastructure (Postgres, Redis, pgAdmin)
- ✅ Added `docker-compose.yml` for full-stack deployment (includes app container)
- ✅ Added `Dockerfile.multistage` for optimized production images with multi-stage builds
- ✅ Dev PostgreSQL mapped to port 5433 (avoids conflict with local PostgreSQL)
- ✅ Production PostgreSQL on standard port 5432
- ✅ Added `.dockerignore` for lean Docker builds
- ✅ Added `start.bat` convenience script for Windows
- ✅ Health checks for PostgreSQL and Redis containers
- ✅ Automatic restart policy for app container

**Profile Management:**
- ✅ Added `PUT /api/v1/drivers/me/profile` - Update driver profile (name, phone, photo)
- ✅ Added `PUT /api/v1/clients/me/profile` - Update client profile (name, phone)
- ✅ Added `GET /api/v1/drivers/{id}` - View driver public profile
- ✅ Added `GET /api/v1/drivers/me/bids` - View driver's submitted bids
- ✅ Added `PUT /api/v1/drivers/me/status` - Update driver availability
- ✅ Added `PUT /api/v1/drivers/me/location` - Update driver GPS location
- ✅ Added FCM token management endpoints for both clients and drivers

---

## Table of Contents
1. [Project Overview](#1-project-overview)
2. [System Architecture](#2-system-architecture)
3. [Technology Stack](#3-technology-stack)
4. [Core Business Logic](#4-core-business-logic)
5. [Backend Structure](#5-backend-structure)
6. [Database Design](#6-database-design)
7. [API Design](#7-api-design)
8. [Security Architecture](#8-security-architecture)
9. [Important Design Decisions](#9-important-design-decisions)
10. [Mobile Application Architecture](#10-mobile-application-architecture)
11. [Development Workflow](#11-development-workflow)
12. [Troubleshooting & Common Issues](#12-troubleshooting--common-issues)
13. [Suggested Improvements](#13-suggested-improvements)
14. [Appendix](#14-appendix)

---

## 1. Project Overview

### What the Project Does
Wasla is a **logistics marketplace platform** that connects clients who need moving services with independent truck drivers. It operates as a bidding platform where:
- Clients post move requests with pickup/dropoff locations
- Nearby drivers receive notifications and submit price bids
- Clients review bids and accept the best offer
- The system tracks job progress from confirmation to completion
- Clients rate drivers after job completion

### Main Problem It Solves
Wasla solves the inefficiency in the moving/logistics industry by:
- **Eliminating middlemen**: Direct connection between clients and drivers
- **Price transparency**: Competitive bidding ensures fair market pricing
- **Real-time matching**: Location-based job discovery for drivers
- **Trust building**: Rating system ensures quality service

### Type of System
This is a **two-sided marketplace platform** (similar to Uber Freight or TaskRabbit for moving services) with:
- Real-time bidding mechanism
- Location-based job matching
- Role-based access control (Client vs Driver)
- Real-time tracking via WebSocket


---

## 2. System Architecture

### Overall Architecture Style
Wasla follows a **Layered Architecture** pattern with clear separation of concerns:

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
│  │ (REST APIs)  │  │ (Validation) │  │ (Transform)  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────────┐
│                    BUSINESS LAYER                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Services   │  │   Mappers    │  │  Schedulers  │      │
│  │ (Logic)      │  │              │  │  (Jobs)      │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────────┐
│                   PERSISTENCE LAYER                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Repositories │  │   Entities   │  │  Flyway      │      │
│  │ (JPA)        │  │   (JPA)      │  │ (Migrations) │      │
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

### Main Components

1. **Authentication Module** (`auth`)
   - Email and password based authentication
   - JWT access token generation and validation
   - Refresh token rotation with database persistence
   - Split registration flow for Client and Driver roles
   - BCrypt password hashing

2. **Job Management Module** (`job`)
   - Job creation and lifecycle management
   - Bid submission and acceptance
   - Status transitions (OPEN → BIDDING → CONFIRMED → IN_PROGRESS → COMPLETED)
   - Automatic job expiry

3. **User Management Module** (`user`)
   - Client profile management
   - Driver profile management
   - Online/offline status tracking

4. **Rating Module** (`rating`)
   - Post-job driver ratings
   - Automatic driver rating average calculation

5. **Location Tracking Module** (`location`)
   - Real-time GPS updates via WebSocket
   - Driver location persistence

6. **Notification Module** (`notification`)
   - Push notifications via Firebase Cloud Messaging (FCM)
   - Event-driven notifications (new job, bid accepted, etc.)


### How Components Interact

**Job Creation Flow:**
```
Client App → JobController → JobService → JobRepository → PostgreSQL
                                  ↓
                          NotificationService → Firebase FCM → Nearby Drivers
```

**Bidding Flow:**
```
Driver App → OfferController → OfferService → BidRepository → PostgreSQL
                                    ↓
                            NotificationService → Firebase FCM → Client
```

**Real-time Location Tracking:**
```
Driver App → WebSocket (/app/driver.location) → LocationWebSocketHandler
                                                        ↓
                                                  DriverRepository
                                                        ↓
                                    WebSocket Broadcast (/topic/job/{id}/location)
                                                        ↓
                                                   Client App
```

---

## 3. Technology Stack

### Core Framework
- **Java 17** - Programming language
- **Spring Boot 3.3.3** - Application framework
- **Maven** - Build tool and dependency management

### Spring Ecosystem
- **Spring Web** - REST API development
- **Spring Data JPA** - Database abstraction layer
- **Spring Security** - Authentication and authorization
- **Spring WebSocket** - Real-time bidirectional communication
- **Spring Validation** - Request validation
- **Spring Data Redis** - Caching layer

### Database & Persistence
- **PostgreSQL 16** - Primary relational database
- **PostGIS 3.4** - Geospatial extension (available but not yet implemented)
- **Flyway** - Database migration management (2 migrations: V1 init, V2 UUID refactor)
- **Hibernate** - JPA implementation
- **HikariCP** - Connection pooling (configured with optimized settings)

### Security & Authentication
- **JJWT 0.13.0** - JWT token generation and validation
- **Spring Security** - Security framework
- **BCrypt** - Password hashing

### Real-time Communication
- **STOMP over WebSocket** - Real-time messaging protocol
- **SockJS** - WebSocket fallback support

### External Services
- **Firebase Cloud Messaging (FCM)** - Push notifications (stub implementation, not yet integrated)
- **Redis 7** - Session storage and caching

### API Documentation
- **SpringDoc OpenAPI 2.6.0** - Swagger/OpenAPI documentation
- **Swagger UI** - Interactive API explorer

### Development Tools
- **Lombok** - Boilerplate code reduction
- **MapStruct 1.5.5** - Compile-time entity-DTO mapper generation
- **Lombok-MapStruct Binding** - Seamless Lombok + MapStruct integration
- **Spring DevTools** - Hot reload during development
- **Docker Compose** - Local infrastructure setup

### Infrastructure (Docker)
- **PostgreSQL 16 + PostGIS 3.4** - Database with geospatial support (postgis/postgis:16-3.4 image)
- **Redis 7 Alpine** - In-memory data store
- **pgAdmin 4** - Database management UI (dpage/pgadmin4:latest)
- **Eclipse Temurin 17** - JRE/JDK base images for app container


---

## 4. Core Business Logic

### 4.1 User Registration & Authentication Flow

**Step-by-Step Process:**

1. **Registration** (`POST /api/v1/auth/register/client` or `/api/v1/auth/register/driver`)
   - User provides full name, email, password, and phone (plus vehicle details for drivers)
   - System checks for email uniqueness
   - Hashes password with BCrypt
   - Saves new user and issues JWT Access & Refresh Token pair
   - Persists Refresh Token in database

2. **Login** (`POST /api/v1/auth/login`)
   - User submits email and password
   - System authenticates credentials via `AuthenticationProvider`
   - Generates new JWT Access & Refresh Token pair
   - Persists Refresh Token and returns both tokens + user info

3. **Token Refresh** (`POST /api/v1/auth/refresh`)
   - Client provides valid refresh token
   - System validates token against database and checks expiry
   - Invalidates previous refresh token (Token Rotation)
   - Issues and persists new Access & Refresh Token pair

4. **Logout** (`POST /api/v1/auth/logout`)
   - Client sends refresh token
   - System deletes it from the database to invalidate the session

**Security Features:**
- Stateless access via JWT and stateful refresh via database tokens
- Secure Password Hashing with BCrypt
- Refresh Token Rotation ensures stolen tokens cannot be reused indefinitely
- Role-based access control (CLIENT, DRIVER, ADMIN)

---

### 4.2 Job Lifecycle Workflow

**Job States:**
```
OPEN → BIDDING → CONFIRMED → IN_PROGRESS → COMPLETED
  ↓
EXPIRED (if no bids accepted within 30 minutes)
```

**Detailed Flow:**

**Phase 1: Job Creation**
1. Client creates job with pickup/dropoff locations and cargo details
2. System sets status to `OPEN` and expiry time (30 minutes from creation)
3. System broadcasts notification to nearby drivers (within 15km radius)
4. Job appears in drivers' "Nearby Jobs" feed

**Phase 2: Bidding**
1. Driver submits bid with price and optional note
2. System validates:
   - Driver is online
   - Job is still OPEN or BIDDING
   - Job hasn't expired
   - Driver hasn't already bid on this job
3. First bid transitions job from `OPEN` → `BIDDING`
4. Client receives push notification about new bid
5. Multiple drivers can bid (unique constraint: one bid per driver per job)

**Phase 3: Bid Acceptance**
1. Client views all bids (sorted by price, lowest first)
2. Client accepts one bid
3. System performs atomic transaction:
   - Accepted bid → `ACCEPTED` status
   - All other pending bids → `WITHDRAWN` status
   - Job → `CONFIRMED` status
   - Assigns driver to job
   - Records accepted price
4. Driver receives "Bid Accepted" notification
5. Client can now see driver's phone number and profile

**Phase 4: Job Execution**
1. Driver updates status to `IN_PROGRESS` when starting
2. Driver sends real-time GPS updates via WebSocket
3. Client tracks driver location in real-time
4. Driver updates status to `COMPLETED` when finished
5. System records completion timestamp

**Phase 5: Rating**
1. Client submits rating (1-5 stars) and optional comment
2. System validates:
   - Job is COMPLETED
   - Client owns the job
   - No existing rating for this job
3. System recalculates driver's average rating
4. Updates driver's `ratingAvg` and `totalJobs` count

**Automatic Expiry:**
- Scheduler runs every 60 seconds
- Expires all jobs where `expiresAt < NOW()` and status is OPEN or BIDDING
- Expired jobs cannot receive new bids


---

### 4.3 Location Tracking Workflow

**Real-time GPS Updates:**

1. **Driver Sends Location** (via WebSocket)
   - Driver app connects to `/ws` endpoint with JWT authentication
   - Sends GPS coordinates to `/app/driver.location`
   - Payload: `{ lat, lng, jobId }`

2. **Server Processing**
   - `LocationWebSocketHandler` receives update
   - Validates driver authentication via WebSocket principal
   - Updates driver's `currentLat` and `currentLng` in database
   - If `jobId` is present, broadcasts to job-specific topic

3. **Client Receives Updates**
   - Client subscribes to `/topic/job/{jobId}/location`
   - Receives real-time driver location updates
   - Updates map UI with driver position

**Configuration:**
- Update interval: 5 seconds (configurable)
- Database sync: 30 seconds (configurable)

---

### 4.4 Notification System

**Event-Driven Notifications:**

| Event | Recipient | Trigger |
|-------|-----------|---------|
| New Job Created | Nearby Drivers | Job creation |
| New Bid Received | Job Owner (Client) | Bid submission |
| Bid Accepted | Winning Driver | Bid acceptance |
| Driver En Route | Client | Status → IN_PROGRESS |
| Job Completed | Client | Status → COMPLETED |

**Implementation:**
- Uses Firebase Cloud Messaging (FCM) - stub implementation
- Stores FCM tokens in `clients.fcm_token` and `drivers.fcm_token`
- **Current State**: Notifications logged to console only (FCM not yet integrated)
- **Future**: Will support data payloads for deep linking (e.g., `jobId`, `type`)

---

### 4.5 Automated Processes

**Job Expiry Scheduler:**
- **Frequency**: Every 60 seconds
- **Action**: Expires jobs where `expiresAt < NOW()` and status is OPEN/BIDDING
- **Implementation**: `@Scheduled(fixedRate = 60_000)` in `JobExpiryScheduler`
- **Query**: Bulk update using custom repository method

**Future Scheduled Tasks** (not yet implemented):
- Expired refresh token cleanup
- Inactive driver status updates

---

## 5. Backend Structure Explanation

### 5.1 Package Organization

```
com.example.wasla/
├── auth/               # Authentication & authorization
│   ├── dto/           # Auth request/response DTOs
│   ├── entity/        # RefreshToken entity
│   ├── repository/    # Auth data access
│   └── security/      # JWT filter, service, helper, UserDetailsService
├── common/            # Shared utilities
│   ├── dto/          # Generic API response wrappers
│   ├── entity/       # Base entity with audit fields
│   └── exception/    # Custom exceptions & global handler
├── config/           # Spring configuration classes
├── job/              # Core business domain
│   ├── controller/  # Job and Bid REST controllers
│   ├── dto/         # Job and Bid DTOs
│   ├── entity/      # Job, Bid, JobStatus, BidStatus
│   ├── mapper/      # MapStruct mapper for Job/Bid
│   ├── repository/  # JPA repositories
│   ├── scheduler/   # Job expiry scheduler
│   └── service/     # Job and Bid business logic
├── location/         # Real-time location tracking
├── notification/     # Push notification service
├── rating/          # Driver rating system
│   ├── dto/
│   ├── entity/
│   ├── mapper/      # MapStruct mapper for Rating
│   └── repository/
└── user/            # User management
    ├── client/      # Client-specific logic
    │   ├── controller/
    │   ├── dto/
    │   ├── entity/
    │   ├── repository/
    │   └── service/
    └── driver/      # Driver-specific logic
        ├── controller/
        ├── dto/
        ├── entity/
        ├── repository/
        └── service/
```

### 5.2 Layer Responsibilities

**Application Entry Point** (`WaslaApplication.java`)
- Main Spring Boot application class
- Annotations:
  - `@SpringBootApplication`: Auto-configuration and component scanning
  - `@EnableJpaAuditing`: Enables automatic timestamp management in BaseEntity
  - `@EnableScheduling`: Enables scheduled tasks (JobExpiryScheduler)

**Controllers** (`*Controller.java`)
- Handle HTTP requests and responses
- Validate request DTOs using `@Valid`
- Delegate business logic to services
- Return standardized `ApiResponse<T>` wrappers
- Define OpenAPI/Swagger documentation
- **No business logic** - pure presentation layer

**Services** (`*Service.java`)
- Contain all business logic and validation
- Orchestrate multiple repository calls
- Handle transactions with `@Transactional`
- Throw domain-specific exceptions
- Call external services (notifications, etc.)
- **Single Responsibility**: Each service manages one domain

**Repositories** (`*Repository.java`)
- Extend `JpaRepository<Entity, UUID>`
- Define custom query methods using Spring Data JPA
- Use `@Query` for complex queries (e.g., geospatial)
- **No business logic** - pure data access

**Entities** (`*.java` in entity packages)
- JPA entities mapped to database tables
- Extend `BaseEntity` for UUID ID and timestamps
- Use Lombok annotations (`@Getter`, `@Setter`, `@Builder`)
- Define relationships (`@ManyToOne`, `@OneToMany`, etc.)
- **Anemic domain model** - no business logic

**DTOs** (Data Transfer Objects)
- Request DTOs: Validate incoming data with Bean Validation
- Response DTOs: Shape outgoing data (hide sensitive fields)
- **Immutability**: Use `@Data` or `@Value` from Lombok
- **Validation**: `@NotNull`, `@NotBlank`, `@Size`, `@DecimalMin`, etc.

**Mappers** (`*Mapper.java`)
- MapStruct interfaces/abstract classes for compile-time code generation
- Handle entity ↔ DTO conversions automatically
- Use `@AfterMapping` for complex conditional logic (e.g., contact info visibility)
- Component model set to `"spring"` for automatic dependency injection
- **Example**: `JobMapper` conditionally includes driver phone only after confirmation via `@AfterMapping`


**Configs** (`config/`)
- `SecurityConfig`: JWT filter chain, role-based endpoint access, CORS
- `WebSocketConfig`: STOMP broker and endpoint registration
- `BeanConfiguration`: AuthenticationProvider, PasswordEncoder, AuthenticationManager beans
- `OpenApiConfig`: Swagger UI customization

---

## 6. Database Design

### 6.1 Entity Relationship Diagram

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│   CLIENTS   │         │    JOBS     │         │   DRIVERS   │
├─────────────┤         ├─────────────┤         ├─────────────┤
│ id (UUID PK)│────┐    │ id (UUID PK)│    ┌────│ id (UUID PK)│
│ full_name   │    │    │ client_id   │    │    │ full_name   │
│ email       │    └───→│ driver_id   │←───┘    │ email       │
│ password    │         │ pickup_*    │         │ password    │
│ phone       │         │ dropoff_*   │         │ phone       │
│ fcm_token   │         │ cargo_desc  │         │ vehicle_type│
│ created_at  │         │ status      │         │ license_plate│
│ updated_at  │         │ accepted_$  │         │ is_available│
└─────────────┘         │ expires_at  │         │ current_lat │
                        │ completed_at│         │ current_lng │
                        └──────┬──────┘         │ rating_avg  │
                               │                │ total_jobs  │
                               │                │ fcm_token   │
                        ┌──────┴──────┐         └──────┬──────┘
                        │    BIDS     │         ┌──────┴─────┐
                        ├─────────────┤         │  RATINGS   │
                        │ id (UUID PK)│         ├────────────┤
                        │ job_id (FK) │         │ id (UUID)  │
                        │ driver_id   │         │ job_id (FK)│
                        │ price       │         │ client_id  │
                        │ note        │         │ driver_id  │
                        │ status      │         │ score      │
                        └─────────────┘         │ comment    │
                                                └────────────┘

┌────────────────┐
│ REFRESH_TOKENS │
├────────────────┤
│ id (UUID PK)   │
│ user_id (UUID) │
│ user_role      │
│ token          │
│ expires_at     │
└────────────────┘
```

### 6.2 Main Entities

**clients**
- **Purpose**: Stores client (customer) profiles and authentication
- **Key Fields**: `email` (unique), `password` (BCrypt hashed), `full_name`, `phone`, `fcm_token`
- **Relationships**: One-to-many with `jobs` and `ratings`

**drivers**
- **Purpose**: Stores driver profiles, vehicle info, and real-time status
- **Key Fields**: `email` (unique), `password`, `full_name`, `phone`, `vehicle_type` (enum), `license_plate` (unique), `is_available`, `current_lat/lng`, `rating_avg`, `total_jobs`
- **Relationships**: One-to-many with `jobs`, `bids`, `ratings`
- **Special**: Tracks availability status and GPS coordinates

**jobs**
- **Purpose**: Core entity representing a move request
- **Key Fields**: 
  - Pickup: `pickup_address`, `pickup_lat`, `pickup_lng`
  - Dropoff: `dropoff_address`, `dropoff_lat`, `dropoff_lng`
  - Status: `status` (OPEN, BIDDING, CONFIRMED, IN_PROGRESS, COMPLETED, EXPIRED, CANCELLED)
  - Pricing: `accepted_price`
  - Timing: `expires_at`, `completed_at`
  - Optional: `cargo_photo_url`
- **Relationships**: 
  - Many-to-one with `clients` (job owner)
  - Many-to-one with `drivers` (assigned driver, nullable until confirmed)
  - One-to-many with `bids`
  - One-to-one with `ratings`

**bids**
- **Purpose**: Driver price quotes on jobs
- **Key Fields**: `price`, `note`, `status` (PENDING, ACCEPTED, WITHDRAWN)
- **Constraints**: Unique constraint on `(job_id, driver_id)` - one bid per driver per job
- **Relationships**: Many-to-one with `jobs` and `drivers`

**ratings**
- **Purpose**: Client feedback on completed jobs
- **Key Fields**: `score` (1-5), `comment`
- **Constraints**: Unique constraint on `job_id` - one rating per job
- **Relationships**: One-to-one with `jobs`, many-to-one with `clients` and `drivers`

**refresh_tokens**
- **Purpose**: Persistent token storage for session revival and rotation
- **Key Fields**: `user_id`, `token`, `expires_at`
- **Lifecycle**: Created on login/register, deleted on logout or upon refreshment (rotation mechanism)

### 6.3 Relationships

**One-to-Many:**
- `Client` → `Jobs` (client_id)
- `Driver` → `Jobs` (driver_id, nullable)
- `Job` → `Bids` (job_id)
- `Driver` → `Bids` (driver_id)
- `Client` → `Ratings` (client_id)
- `Driver` → `Ratings` (driver_id)

**One-to-One:**
- `Job` → `Rating` (job_id, unique)

**Indexes:**
- `jobs`: `status`, `client_id`, `driver_id`, `expires_at` (for efficient queries)
- `bids`: `job_id`, `driver_id` (for bid lookups)
- `refresh_tokens`: `token`, `user_id` (for refresh token validation and lookup)

### 6.4 Audit Fields (BaseEntity)

All entities inherit from `BaseEntity`:
- `id` (UUID, auto-generated)
- `created_at` (timestamp, auto-set on insert)
- `updated_at` (timestamp, auto-updated on modification)

**Benefits:**
- Consistent primary key strategy
- Automatic audit trail
- No manual timestamp management

### 6.5 Database Migrations

**Flyway Migration History:**

**V1__init_schema.sql** - Initial schema with BIGSERIAL IDs
- Created unified `users` table with role-based access
- Created `driver_profiles` as one-to-one extension of users
- Created `jobs`, `bids`, `ratings`, `refresh_tokens` tables
- Used BIGSERIAL (auto-increment) for primary keys
- Included audit fields (is_deleted, created_at, updated_at, created_by, last_modified_by)

**V2__refactor_to_separate_entities.sql** - Major refactoring to UUID-based separate entities
- Split `users` table into separate `clients` and `drivers` tables
- Merged `driver_profiles` into `drivers` table (denormalized for performance)
- Migrated all foreign key relationships to new UUID-based IDs
- Removed soft delete fields (is_deleted) - switched to hard deletes
- Removed audit fields (created_by, last_modified_by) - kept only timestamps
- Created temporary mapping tables for ID conversion during migration
- Enabled UUID generation with `uuid-ossp` and `pgcrypto` extensions
- Preserved all data integrity during migration

**Migration Strategy:**
- Baseline on migrate enabled for existing databases
- Migrations run automatically on application startup
- Manual execution: `./mvnw flyway:migrate`
- Rollback not supported (Flyway limitation) - use database backups for recovery


---

## 7. API Design

### 7.1 Authentication Endpoints

**Base Path:** `/api/v1/auth` (Public - No JWT required)

| Endpoint | Method | Purpose | Request | Response |
|----------|--------|---------|---------|----------|
| `/register/client` | POST | Register new client | `{ fullName, email, phone, password }` | `{ accessToken, refreshToken, expiresIn, user }` |
| `/register/driver` | POST | Register new driver | `{ fullName, email, phone, password, vehicleType, licensePlate }` | `{ accessToken, refreshToken, expiresIn, user }` |
| `/login`       | POST | Login existing user | `{ email, password }` | `{ accessToken, refreshToken, expiresIn, user }` |
| `/refresh`     | POST | Refresh auth token  | `{ refreshToken }` | `{ accessToken, refreshToken, expiresIn, user }` |
| `/logout`      | POST | Invalidate refresh token | `{ refreshToken }` | Success message |

**Example Request - Login:**
```json
POST /api/v1/auth/login
{
  "email": "user@example.com",
  "password": "Password123!"
}
```

**Example Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzM4...",
    "refreshToken": "dO9y28nfG_2...",
    "expiresIn": 3600,
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "email": "user@example.com",
      "phone": "+1234567890",
      "role": "CLIENT",
      "newUser": false
    }
  }
}
```

**Token Lifecycle:** Access tokens are short-lived, while Refresh tokens are long-lived and valid for 30 days.

---

### 7.2 Job Endpoints

**Base Path:** `/api/v1/jobs` (Requires JWT)

| Endpoint | Method | Role | Purpose | Request | Response |
|----------|--------|------|---------|---------|----------|
| `/` | POST | CLIENT | Create job | `CreateJobRequest` | `JobResponseDto` |
| `/{id}` | GET | Any | Get job details | - | `JobResponseDto` |
| `/my` | GET | Any | Get my jobs (paginated) | `?page=0&size=20` | `Page<JobListDto>` |
| `/nearby` | GET | DRIVER | Find nearby jobs | `?lat=&lng=&radiusKm=15` | `List<JobListDto>` |
| `/{id}/status` | PATCH | DRIVER | Update job status | `{ status }` | `JobResponseDto` |

**Example Request - Create Job:**
```json
POST /api/v1/jobs
Authorization: Bearer <access_token>
{
  "pickupAddress": "123 Main St, City",
  "pickupLat": 40.7128,
  "pickupLng": -74.0060,
  "dropoffAddress": "456 Oak Ave, Town",
  "dropoffLat": 40.7580,
  "dropoffLng": -73.9855,
  "cargoDesc": "2 bedroom apartment furniture",
  "cargoPhotoUrl": "https://example.com/photo.jpg"
}
```

**Example Response:**
```json
{
  "success": true,
  "message": "Job created successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "clientId": "660e8400-e29b-41d4-a716-446655440001",
    "pickupAddress": "123 Main St, City",
    "pickupLat": 40.7128,
    "pickupLng": -74.0060,
    "dropoffAddress": "456 Oak Ave, Town",
    "dropoffLat": 40.7580,
    "dropoffLng": -73.9855,
    "cargoDesc": "2 bedroom apartment furniture",
    "status": "OPEN",
    "expiresAt": "2026-03-08T15:30:00",
    "createdAt": "2026-03-08T15:00:00",
    "bidCount": 0
  }
}
```

---

### 7.3 Bid Endpoints

**Base Path:** `/api/v1/jobs/{jobId}/bids` (Requires JWT)

| Endpoint | Method | Role | Purpose | Request | Response |
|----------|--------|------|---------|---------|----------|
| `/` | POST | DRIVER | Submit bid | `{ price, note }` | `BidResponseDto` |
| `/` | GET | Any | Get all bids for job | - | `List<BidResponseDto>` |
| `/{bidId}/accept` | PATCH | CLIENT | Accept bid | - | `JobResponseDto` |

**Example Request - Submit Bid:**
```json
POST /api/v1/jobs/550e8400-e29b-41d4-a716-446655440000/bids
Authorization: Bearer <driver_access_token>
{
  "price": 150.00,
  "note": "I have a large truck and can do this today"
}
```

**Example Response:**
```json
{
  "success": true,
  "message": "Bid submitted successfully",
  "data": {
    "id": "770e8400-e29b-41d4-a716-446655440002",
    "jobId": "550e8400-e29b-41d4-a716-446655440000",
    "price": 150.00,
    "note": "I have a large truck and can do this today",
    "status": "PENDING",
    "createdAt": "2026-03-08T15:05:00",
    "driver": {
      "id": "880e8400-e29b-41d4-a716-446655440003",
      "name": "John Driver",
      "vehicleType": "PICKUP_ONE_TON",
      "ratingAvg": 4.8,
      "totalJobs": 45
    }
  }
}
```

---

### 7.4 User Profile Endpoints

**Client Profile:** `/api/v1/clients` (Requires CLIENT role)

| Endpoint | Method | Purpose | Request | Response |
|----------|--------|---------|---------|----------|
| `/me` | GET | Get own profile | - | `ClientProfileDto` |
| `/me/profile` | PUT | Update profile | `{ fullName, phone }` | `ClientProfileDto` |
| `/me/fcm-token` | PUT | Update FCM token | `<fcmToken string>` | Success message |

**Driver Profile:** `/api/v1/drivers` (Requires DRIVER role, except `/{id}`)

| Endpoint | Method | Purpose | Request | Response |
|----------|--------|---------|---------|----------|
| `/me` | GET | Get own profile | - | `DriverProfileDto` |
| `/me/profile` | PUT | Update profile | `{ fullName, phone, photoUrl }` | `DriverProfileDto` |
| `/me/status` | PATCH | Update availability | `{ isAvailable }` | `DriverProfileDto` |
| `/me/location` | PUT | Update GPS location | `{ lat, lng }` | Success message |
| `/me/fcm-token` | PUT | Update FCM token | `<fcmToken string>` | Success message |
| `/me/bids` | GET | Get my bids | - | `List<BidResponseDto>` |
| `/{id}` | GET | Get public profile (any auth user) | - | `DriverProfileDto` (limited) |

**Example Request - Update Driver Profile:**
```json
PUT /api/v1/drivers/me/profile
Authorization: Bearer <driver_access_token>
{
  "fullName": "John Smith",
  "phone": "+201012345678",
  "photoUrl": "https://example.com/photo.jpg"
}
```

**Example Response:**
```json
{
  "success": true,
  "message": "Profile updated",
  "data": {
    "id": "880e8400-e29b-41d4-a716-446655440003",
    "fullName": "John Smith",
    "email": "john@example.com",
    "phone": "+201012345678",
    "vehicleType": "PICKUP_ONE_TON",
    "licensePlate": "ABC 123",
    "photoUrl": "https://example.com/photo.jpg",
    "isAvailable": true,
    "currentLat": 30.0444,
    "currentLng": 31.2357,
    "ratingAvg": 4.80,
    "totalJobs": 45
  }
}
```

---

### 7.5 Rating Endpoints

**Base Path:** `/api/v1/jobs/{jobId}/rating` (Requires CLIENT role)

| Endpoint | Method | Purpose | Request | Response |
|----------|--------|---------|---------|----------|
| `/` | POST | Submit rating | `{ score, comment }` | `RatingResponseDto` |
| `/` | GET | Get job rating | - | `RatingResponseDto` |

**Example Request:**
```json
POST /api/v1/jobs/550e8400-e29b-41d4-a716-446655440000/rating
Authorization: Bearer <client_access_token>
{
  "score": 5,
  "comment": "Excellent service, very professional!"
}
```

**Important:** The field name is `score` (not `rating`). Valid values are 1-5.

---

### 7.6 WebSocket Endpoints

**Connection:** `ws://localhost:8080/ws` (with SockJS fallback)

**Authentication:** JWT token passed via WebSocket handshake

**Channels:**

| Type | Destination | Purpose | Direction |
|------|-------------|---------|-----------|
| Send | `/app/driver.location` | Driver sends GPS update | Client → Server |
| Subscribe | `/topic/job/{jobId}/location` | Client receives driver location | Server → Client |

**Example - Driver Location Update:**
```json
// Driver sends to /app/driver.location
{
  "lat": 40.7580,
  "lng": -73.9855,
  "jobId": "550e8400-e29b-41d4-a716-446655440000"
}

// Client receives on /topic/job/550e8400-e29b-41d4-a716-446655440000/location
{
  "lat": 40.7580,
  "lng": -73.9855,
  "jobId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 7.7 API Response Format

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* response payload */ }
}
```

**Error Response:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid or expired OTP",
  "path": "/api/v1/auth/verify-otp",
  "timestamp": "2026-03-08T15:00:00"
}
```

**Validation Error Response:**
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more fields have invalid values",
  "path": "/api/v1/jobs",
  "timestamp": "2026-03-08T15:00:00",
  "fieldErrors": [
    {
      "field": "pickupLat",
      "message": "Pickup latitude is required",
      "rejectedValue": null
    }
  ]
}
```


---

## 8. Security Architecture

### 8.1 Authentication Flow

**JWT-Based Stateless Authentication:**

```
┌─────────────┐                                    ┌─────────────┐
│   Client    │                                    │   Server    │
└──────┬──────┘                                    └──────┬──────┘
       │                                                  │
       │  1. POST /auth/register/client or /driver        │
       │     { fullName, email, phone, password }         │
       ├────────────────────────────────────────────────>│
       │                                                  │
       │  2. { accessToken, refreshToken, user }          │
       │<────────────────────────────────────────────────┤
       │                                                  │
       │  3. POST /auth/login                            │
       │     { email, password }                          │
       ├────────────────────────────────────────────────>│
       │                                                  │
       │  4. { accessToken, refreshToken, user }          │
       │<────────────────────────────────────────────────┤
       │                                                  │
       │  5. Subsequent requests with JWT                │
       │     Authorization: Bearer <accessToken>         │
       ├────────────────────────────────────────────────>│
       │                                                  │
       │  6. JwtAuthFilter validates token               │
       │     - Extracts email from token subject          │
       │     - Loads UserDetails from database            │
       │     - Sets SecurityContext                      │
       │                                                  │
       │  7. Response                                    │
       │<────────────────────────────────────────────────┤
       │                                                  │
       │  8. When access token expires:                  │
       │     POST /auth/refresh { refreshToken }         │
       │  9. New { accessToken, refreshToken }            │
       │     (old refresh token invalidated)              │
```

**Token Lifecycle:** Access tokens expire after 7 days (configurable via `jwt.access-token-expiration`). Refresh tokens expire after 30 days. When access token expires, use the refresh token endpoint to get new tokens.

### 8.2 JWT Token Structure

**Access Token Claims:**
```json
{
  "sub": "user@example.com",  // user email (subject)
  "iat": 1709913600,  // issued at
  "exp": 1710518400   // expires at (7 days)
}
```

**Token Security:**
- Signed with HMAC-SHA384 using 256-bit secret key
- Secret stored in environment variable `JWT_SECRET`
- Tokens are stateless (no server-side storage)

### 8.3 Authorization Model

**Role-Based Access Control (RBAC):**

| Role | Authorities | Description |
|------|-------------|-------------|
| `CLIENT` | Client operations | Can create jobs, accept bids, rate drivers |
| `DRIVER` | Driver operations | Can view nearby jobs, submit bids, update job status |
| `ADMIN` | Admin operations | Full system access (future) |

**Endpoint Protection (SecurityConfig):**

```java
// Public endpoints
.requestMatchers("/api/v1/auth/**").permitAll()
.requestMatchers("/ws/**").permitAll()
.requestMatchers("/swagger-ui/**", "/api-docs/**",
        "/swagger-ui.html", "/v3/api-docs/**").permitAll()
.requestMatchers("/actuator/**").permitAll()

// Admin only
.requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")

// Client only
.requestMatchers(HttpMethod.POST, "/api/v1/jobs").hasAuthority("CLIENT")
.requestMatchers(HttpMethod.PATCH, "/api/v1/jobs/*/bids/*/accept").hasAuthority("CLIENT")
.requestMatchers(HttpMethod.POST, "/api/v1/jobs/*/rating").hasAuthority("CLIENT")
.requestMatchers("/api/v1/clients/**").hasAuthority("CLIENT")

// Driver only
.requestMatchers(HttpMethod.GET, "/api/v1/jobs/nearby").hasAuthority("DRIVER")
.requestMatchers(HttpMethod.POST, "/api/v1/jobs/*/bids").hasAuthority("DRIVER")
.requestMatchers("/api/v1/drivers/me/**").hasAuthority("DRIVER")
.requestMatchers(HttpMethod.GET, "/api/v1/drivers/me").hasAuthority("DRIVER")

// Any authenticated user
.requestMatchers(HttpMethod.GET, "/api/v1/drivers/*").authenticated()
.anyRequest().authenticated()
```

### 8.4 Request Filtering

**JwtAuthFilter (OncePerRequestFilter):**

1. Extracts JWT from `Authorization: Bearer <token>` header
2. Validates token signature and expiry
3. Extracts user email from token subject
4. Loads `UserDetails` via `CustomUserDetailsService`
5. Creates `UsernamePasswordAuthenticationToken` and sets `SecurityContext`
6. Subsequent filters and controllers access authenticated user via `SecurityContextHolder`

**Helper for Current User:**
```java
@Component
public class SecurityHelper {
    public UUID getCurrentUserId() {
        // Extracts current user ID from SecurityContext
    }
    
    public String getCurrentUserRole() {
        // Returns the authority (CLIENT, DRIVER, ADMIN)
    }
}
```

### 8.5 Security Best Practices Implemented

✅ **Stateless Authentication**: No server-side sessions, horizontally scalable  
✅ **Token Rotation**: Refresh tokens rotated on use to prevent reuse  
✅ **Password Hashing**: BCrypt for secure password storage  
✅ **CORS Configuration**: Controlled cross-origin access  
✅ **CSRF Protection**: Disabled (stateless JWT, not cookie-based)  
✅ **Email & Password Auth**: Secure credential-based authentication  
✅ **Role-Based Access**: Fine-grained endpoint protection (CLIENT, DRIVER, ADMIN)  
✅ **Input Validation**: Bean Validation on all DTOs  
✅ **SQL Injection Prevention**: JPA parameterized queries  

### 8.6 WebSocket Security

**Authentication:**
- JWT token passed during WebSocket handshake
- `WebSocketAuthConfig` intercepts handshake and validates token
- Sets user principal for authenticated connections
- Unauthenticated connections are rejected

**Authorization:**
- Only authenticated drivers can send location updates
- Only authenticated clients can subscribe to job-specific location topics
- Server validates user identity before broadcasting

---

## 9. Important Design Decisions

### 9.1 DTO Pattern

**Why DTOs?**
- **Decoupling**: API contracts independent of database schema
- **Security**: Hide sensitive fields (e.g., driver phone until job confirmed)
- **Validation**: Centralized input validation with Bean Validation
- **Versioning**: Easy to evolve API without breaking database

**Example - Conditional Field Exposure:**
```java
// JobMapper.toResponseDto()
if (job.getStatus() == JobStatus.CONFIRMED 
    || job.getStatus() == JobStatus.IN_PROGRESS 
    || job.getStatus() == JobStatus.COMPLETED) {
    builder.driverPhone(job.getDriver().getPhone());
}
```
Driver's phone number is only revealed after bid acceptance, protecting privacy.

### 9.2 Mapper Pattern

**Why MapStruct?**
- **Type Safety**: Compile-time code generation catches mapping errors early
- **Performance**: No runtime reflection overhead (unlike ModelMapper)
- **Lombok Integration**: Seamless integration via `lombok-mapstruct-binding`
- **Conditional Logic**: `@AfterMapping` methods for complex transformations (e.g., phone number exposure)
- **Spring Integration**: Component model `"spring"` for automatic DI

**Example - MapStruct with AfterMapping:**
```java
@Mapper(componentModel = "spring")
public abstract class JobMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(target = "driverPhone", ignore = true)
    public abstract JobResponseDto toResponseDto(Job job);

    @AfterMapping
    protected void enrichContactInfo(Job job, @MappingTarget JobResponseDto dto) {
        if (isConfirmedOrLater(job.getStatus())) {
            dto.setDriverPhone(job.getDriver().getPhone());
        }
    }
}
```

### 9.3 UUID Primary Keys

**Why UUIDs instead of Auto-Increment IDs?**
- **Distributed Systems**: No coordination needed for ID generation
- **Security**: Non-sequential, harder to enumerate resources
- **Merging**: Easy to merge data from multiple sources
- **Client-Side Generation**: Clients can generate IDs before server confirmation

**Trade-off**: Slightly larger storage (16 bytes vs 4-8 bytes), but negligible for this scale.

### 9.4 Soft Delete (Not Implemented)

**Current Approach**: Hard delete (actual row deletion)

**Why Not Soft Delete?**
- **MVP Simplicity**: No requirement for data recovery or audit trail
- **GDPR Compliance**: Hard delete supports "right to be forgotten"

**Future Consideration**: Add `deleted_at` column for audit requirements.

### 9.5 Audit Timestamps (BaseEntity)

**Why Automatic Timestamps?**
- **Consistency**: All entities have creation and modification tracking
- **Debugging**: Easy to trace when records were created/updated
- **Business Logic**: Can query "jobs created in last 24 hours"
- **No Manual Management**: Hibernate handles it automatically

**Implementation:**
```java
@CreationTimestamp
private LocalDateTime createdAt;

@UpdateTimestamp
private LocalDateTime updatedAt;
```

### 9.6 Validation Strategy

**Three Layers of Validation:**

1. **DTO Validation** (Bean Validation)
   - Syntax validation: `@NotNull`, `@NotBlank`, `@Size`, `@DecimalMin`
   - Runs before controller method execution
   - Returns 400 Bad Request with field errors

2. **Business Validation** (Service Layer)
   - Semantic validation: "Driver must be online to bid"
   - Throws domain exceptions: `BadRequestException`, `UnauthorizedException`
   - Returns 400/401/403 with business error message

3. **Database Constraints**
   - Unique constraints: `(job_id, driver_id)` for bids
   - Foreign key constraints: Referential integrity
   - Check constraints: `score BETWEEN 1 AND 5`
   - Last line of defense against data corruption

### 9.7 Exception Handling Strategy

**Global Exception Handler** (`@RestControllerAdvice`):
- Catches all exceptions from controllers
- Converts to standardized `ApiError` response
- Logs errors with appropriate severity
- Prevents stack trace leakage to clients

**Custom Exception Hierarchy:**
```
WaslaAppException (abstract)
├── BadRequestException (400)
├── UnauthorizedException (401)
└── ResourceNotFoundException (404)
```

**Benefits:**
- Consistent error responses across all endpoints
- Easy to add new exception types
- Centralized logging and monitoring

### 9.8 Transaction Management

**Declarative Transactions:**
```java
@Transactional  // Read-write transaction
public JobResponseDto createJob(CreateJobDto dto) { ... }

@Transactional(readOnly = true)  // Read-only optimization
public JobResponseDto getJobById(UUID jobId) { ... }
```

**Why `@Transactional`?**
- **Atomicity**: Multiple database operations succeed or fail together
- **Consistency**: Prevents partial updates
- **Lazy Loading**: Entities can load relationships within transaction
- **Read-Only Optimization**: Hibernate skips dirty checking

**Example - Bid Acceptance (Atomic):**
```java
@Transactional
public OfferResponseDto acceptBid(UUID jobId, UUID bidId) {
    // 1. Accept this bid
    bid.setStatus(OfferStatus.ACCEPTED);
    
    // 2. Withdraw other bids
    bidRepository.withdrawOtherBids(jobId, bidId);
    
    // 3. Confirm job
    job.setStatus(JobStatus.CONFIRMED);
    job.setDriver(bid.getDriver());
    
    // All or nothing - if any step fails, entire transaction rolls back
}
```

### 9.9 Geospatial Queries

**Current Implementation:**
- PostGIS 3.4 extension is available in the PostgreSQL container
- **Not yet implemented**: Nearby jobs currently return all open jobs without distance filtering
- TODO: Implement ST_DWithin spatial query for accurate radius-based job discovery

**Planned PostGIS Implementation:**
```sql
-- Find jobs within radius (using ST_DWithin)
SELECT * FROM jobs 
WHERE status = 'OPEN' 
  AND ST_DWithin(
    ST_MakePoint(pickup_lng, pickup_lat)::geography,
    ST_MakePoint(:lng, :lat)::geography,
    :radiusMeters
  );
```

**Why PostGIS?**
- **Accuracy**: Proper spherical distance calculation (not Euclidean)
- **Performance**: Spatial indexes for fast queries
- **Standard**: Industry-standard geospatial extension

**Alternative Considered**: Haversine formula in application code (rejected due to performance).

### 9.10 Scheduled Tasks

**Spring Scheduling:**
```java
@EnableScheduling  // Enable in main application class
@Scheduled(fixedRate = 60_000)  // Every 60 seconds
public void expireJobs() { ... }
```

**Why Fixed Rate?**
- **Simplicity**: No need for external scheduler (Quartz, cron)
- **Embedded**: Runs within application process
- **Sufficient**: Job expiry doesn't need sub-second precision

**Trade-off**: Not suitable for distributed deployments (multiple instances would run duplicate tasks). Future: Use distributed lock (Redis) or external scheduler.


---

## 10. Mobile Application Architecture

### 10.1 Technology Stack

**Framework & Language:**
- **Flutter 3.x** - Cross-platform mobile framework
- **Dart** - Programming language

**State Management:**
- **Provider** - Lightweight state management solution
- Providers for: Auth, Jobs, Profile, Bids

**Networking:**
- **Dio** - HTTP client with interceptors
- **web_socket_channel** - WebSocket support for real-time features

**Storage:**
- **shared_preferences** - Local storage for tokens and user data

**UI Components:**
- **Material Design 3** - Modern UI components
- **Google Maps Flutter** - Map integration for location features

### 10.2 Application Structure

```
lib/
├── core/
│   ├── api/
│   │   └── api_client.dart          # Dio HTTP client with JWT interceptor
│   ├── router/
│   │   └── app_router.dart          # GoRouter navigation configuration
│   ├── storage/
│   │   └── storage_service.dart     # SharedPreferences wrapper
│   └── theme/
│       └── app_theme.dart           # Material Design theme
├── features/
│   ├── auth/
│   │   ├── auth_provider.dart       # Authentication state management
│   │   ├── auth_repository.dart     # Auth API calls
│   │   └── screens/
│   │       ├── role_selection_screen.dart
│   │       ├── phone_input_screen.dart
│   │       └── otp_screen.dart
│   ├── jobs/
│   │   ├── job_provider.dart        # Job state management
│   │   ├── job_repository.dart      # Job API calls
│   │   └── screens/
│   │       ├── client_home_screen.dart
│   │       ├── driver_home_screen.dart
│   │       ├── create_job_screen.dart
│   │       ├── job_details_screen.dart
│   │       └── job_bids_screen.dart
│   ├── profile/
│   │   ├── profile_provider.dart    # Profile state management
│   │   ├── profile_repository.dart  # Profile API calls
│   │   └── screens/
│   │       ├── profile_screen.dart
│   │       ├── edit_driver_profile_screen.dart
│   │       └── edit_client_profile_screen.dart
│   ├── driver/
│   │   └── screens/
│   │       └── driver_public_profile_screen.dart
│   └── rating/
│       └── screens/
│           └── rate_driver_screen.dart
└── main.dart                        # App entry point
```

### 10.3 Key Features by Screen

**Authentication Flow:**
1. **Role Selection Screen** - Choose CLIENT or DRIVER role
2. **Phone Input Screen** - Enter phone number with country code (+20 for Egypt)
3. **OTP Screen** - Verify 6-digit OTP code

**Client Screens:**
1. **Client Home** - View my jobs (tabs: Active, Completed)
2. **Create Job** - Create new moving job with pickup/dropoff locations
3. **Job Details** - View job details, bids, and contact info (after acceptance)
4. **Job Bids** - View and accept driver bids
5. **Rate Driver** - Submit 1-5 star rating after job completion
6. **Profile** - View and edit client profile

**Driver Screens:**
1. **Driver Home** - View available jobs and my jobs (tabs: Available, My Jobs, My Bids)
2. **Job Details** - View job details, submit bid, update status, view contact info
3. **Driver Public Profile** - View driver's public profile (ratings, truck info)
4. **Profile** - View and edit driver profile (name, truck type, plate number)

### 10.4 State Management Pattern

**Provider Pattern:**
```dart
// Example: AuthProvider
class AuthProvider extends ChangeNotifier {
  bool _isAuthenticated = false;
  String? _accessToken;
  User? _user;

  Future<void> verifyOtp(String phone, String otp, String role) async {
    final response = await _authRepository.verifyOtp(phone, otp, role);
    _accessToken = response.accessToken;
    _user = response.user;
    _isAuthenticated = true;
    await _storageService.saveToken(_accessToken!);
    notifyListeners();
  }
}
```

**Usage in Widgets:**
```dart
// Listen to state changes
final authProvider = Provider.of<AuthProvider>(context);

// Access state without listening
final authProvider = Provider.of<AuthProvider>(context, listen: false);
```

### 10.5 API Integration

**HTTP Client Configuration:**
```dart
// Dio with JWT interceptor
final dio = Dio(BaseOptions(
  baseUrl: 'http://localhost:8080',
  connectTimeout: Duration(seconds: 15),
  receiveTimeout: Duration(seconds: 15),
));

// Add JWT token to all requests
dio.interceptors.add(InterceptorsWrapper(
  onRequest: (options, handler) {
    final token = await storageService.getAccessToken();
    if (token != null) {
      options.headers['Authorization'] = 'Bearer $token';
    }
    return handler.next(options);
  },
));
```

**Repository Pattern:**
```dart
class JobRepository {
  final ApiClient _apiClient;

  Future<JobResponse> createJob(CreateJobDto dto) async {
    final response = await _apiClient.post('/api/v1/jobs', data: dto.toJson());
    return JobResponse.fromJson(response.data['data']);
  }
}
```

### 10.6 Navigation

**GoRouter Configuration:**
```dart
final router = GoRouter(
  initialLocation: '/role-selection',
  routes: [
    GoRoute(path: '/role-selection', builder: (context, state) => RoleSelectionScreen()),
    GoRoute(path: '/phone-input', builder: (context, state) => PhoneInputScreen()),
    GoRoute(path: '/otp', builder: (context, state) => OtpScreen()),
    GoRoute(path: '/client-home', builder: (context, state) => ClientHomeScreen()),
    GoRoute(path: '/driver-home', builder: (context, state) => DriverHomeScreen()),
    GoRoute(path: '/job/:id', builder: (context, state) => JobDetailsScreen(jobId: state.params['id']!)),
    // ... more routes
  ],
);
```

**Navigation Usage:**
```dart
// Navigate to screen
context.go('/client-home');

// Navigate with parameters
context.go('/job/${jobId}');

// Go back
context.pop();
```

### 10.7 Contact Information Display

**Rules:**
- Contact info (name, phone) only visible when job status is `CONFIRMED` or later
- If user hasn't set name in profile, shows "Name not set" placeholder
- Phone number always shown (critical for communication)
- Refresh button available to reload job details

**Implementation:**
```dart
// Contact card only shown when phone is available
if (contactPhone != null && contactPhone.isNotEmpty) {
  ContactCard(
    name: contactName ?? 'Name not set',
    phone: contactPhone,
    role: isDriver ? 'Client' : 'Driver',
  );
}
```

### 10.8 Error Handling

**Three Levels:**
1. **Network Errors** - Connection timeout, no internet
2. **API Errors** - 400/401/403/500 responses
3. **Validation Errors** - Form validation before submission

**User Feedback:**
```dart
try {
  await jobProvider.createJob(dto);
  ScaffoldMessenger.of(context).showSnackBar(
    SnackBar(content: Text('Job created successfully')),
  );
  context.go('/client-home');
} catch (e) {
  ScaffoldMessenger.of(context).showSnackBar(
    SnackBar(content: Text('Error: ${e.toString()}')),
  );
}
```

### 10.9 Real-time Features

**WebSocket Integration:**
```dart
// Connect to WebSocket
final channel = WebSocketChannel.connect(
  Uri.parse('ws://localhost:8080/ws'),
);

// Subscribe to location updates
stompClient.subscribe(
  destination: '/topic/job/$jobId/location',
  callback: (frame) {
    final location = jsonDecode(frame.body);
    updateDriverLocation(location['lat'], location['lng']);
  },
);

// Send location update (driver)
stompClient.send(
  destination: '/app/driver.location',
  body: jsonEncode({'lat': lat, 'lng': lng, 'jobId': jobId}),
);
```

### 10.10 Common Issues & Solutions

**Issue: Phone format validation**
- Always include country code: `+20` for Egypt, `+1` for US
- Example: `+201062315558`

**Issue: Navigation after OTP**
- Fixed: Navigation happens immediately after successful verification
- No more mounted checks or delays

**Issue: Contact info not showing**
- Ensure job status is `CONFIRMED` or later
- User must set name in profile for name to display
- Phone number always shown if available

**Issue: Rating submission fails**
- Use field name `score` (not `rating`)
- Valid values: 1-5

---

## 11. Development Workflow

### 11.1 Prerequisites

**Required Software:**
- **Java 17** (JDK) - [Download](https://adoptium.net/)
- **Maven 3.8+** - Included via Maven Wrapper (`mvnw`)
- **Docker & Docker Compose** - For local infrastructure
- **Git** - Version control
- **IDE** - IntelliJ IDEA (recommended) or VS Code with Java extensions

**Optional:**
- **Postman** or **Insomnia** - API testing
- **pgAdmin** - Database management (included in Docker Compose)

### 11.2 Initial Setup

**1. Clone Repository:**
```bash
git clone <repository-url>
cd wasla-backend/wasla
```

**2. Start Dev Infrastructure (PostgreSQL + Redis + pgAdmin):**

**Option A: Dev Infrastructure Only (Recommended for Development)**
```bash
docker-compose -f docker-compose-dev.yml up -d
```

This starts:
- PostgreSQL (PostGIS 16-3.4) on `localhost:5433` (port 5433 to avoid conflict with local PostgreSQL)
- Redis 7 on `localhost:6379`
- pgAdmin on `localhost:5050` (admin@wasla.app / admin)

Then run the app locally from your IDE or:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Option B: Full Stack (App + Infrastructure)**
```bash
docker-compose up --build
```

This starts everything including the Spring Boot app on `localhost:8080`.

**Option C: Windows Convenience Script**
```cmd
start.bat
```

This automatically:
1. Starts Docker containers (full stack via docker-compose.yml)
2. Waits 10 seconds for initialization
3. Runs Spring Boot application with Maven

**3. Configure Environment Variables:**

For local development with `docker-compose-dev.yml`, update `application-dev.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/wasla  # Note: port 5433 for dev
    username: wasla
    password: secret
```

For production or full Docker stack, set environment variables:
```bash
# Database (production uses port 5432)
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/wasla
SPRING_DATASOURCE_USERNAME=wasla
SPRING_DATASOURCE_PASSWORD=secret

# Redis
SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379

# JWT Secret (256-bit minimum)
JWT_SECRET=waslaDefaultSecretKeyForDevelopmentOnly2026MustBeAtLeast256Bits

# Active Profile
SPRING_PROFILES_ACTIVE=prod
```

**4. Run Database Migrations:**

Migrations run automatically on application startup (Flyway enabled by default).

To run manually:
```bash
./mvnw flyway:migrate
```

To check migration status:
```bash
./mvnw flyway:info
```

**Current Migrations:**
- V1: Initial schema with BIGSERIAL IDs
- V2: Refactor to separate Client/Driver entities with UUID

**5. Build Project:**
```bash
./mvnw clean install
```

**6. Run Application:**

**Option A: Using Maven (Recommended for Development)**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

On Windows:
```cmd
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

**Option B: Using Windows Convenience Script**
```cmd
start.bat
```
This script automatically starts Docker containers and runs the application.

**Option C: From IDE**
- Open `WaslaApplication.java`
- Right-click → Run
- Set VM options: `-Dspring.profiles.active=dev`

**Option D: Using JAR**
```bash
./mvnw clean package
java -jar target/wasla-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

**7. Verify Startup:**
- Application: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/api-docs`
- Health Check: `http://localhost:8080/actuator/health`
- pgAdmin: `http://localhost:5050` (admin@wasla.app / admin)

**Expected Console Output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.3.3)

INFO  c.e.wasla.WaslaApplication - Starting WaslaApplication
INFO  c.e.wasla.WaslaApplication - The following 1 profile is active: "dev"
INFO  o.f.c.i.d.base.BaseDatabaseType - Database: jdbc:postgresql://localhost:5433/wasla
INFO  o.f.core.internal.command.DbValidate - Successfully validated 2 migrations
INFO  o.s.b.w.embedded.tomcat.TomcatWebServer - Tomcat started on port 8080
INFO  c.e.wasla.WaslaApplication - Started WaslaApplication in 3.456 seconds
```

### 11.3 Development Commands

**Run Tests:**
```bash
./mvnw test
```

**Run with Hot Reload (DevTools):**
```bash
./mvnw spring-boot:run
```
Changes to Java files trigger automatic restart.

**Build JAR:**
```bash
./mvnw clean package
java -jar target/wasla-0.0.1-SNAPSHOT.jar
```

**Database Management:**
```bash
# Access PostgreSQL CLI
docker exec -it wasla-postgres-dev psql -U wasla -d wasla

# View logs
docker-compose -f docker-compose-dev.yml logs -f postgres

# Reset database (WARNING: deletes all data)
docker-compose -f docker-compose-dev.yml down -v
docker-compose -f docker-compose-dev.yml up -d
```

**Redis CLI:**
```bash
docker exec -it wasla-redis-dev redis-cli
```

### 11.4 Configuration Profiles

**Available Profiles:**

**application.yaml** - Base configuration
- Default database: `localhost:5432`
- Default Redis: `localhost:6379`
- HikariCP connection pool: max 10, min idle 5
- JWT expiration: 7 days (access), 30 days (refresh)
- Flyway enabled with baseline-on-migrate
- Logging: DEBUG for application, WARN for security

**application-dev.yml** - Development profile
- Database: `localhost:5432/wasla` (update to 5433 if using docker-compose-dev.yml)
- Credentials: wasla/wasla123
- Verbose logging: DEBUG for all components, TRACE for SQL bindings
- SQL formatting enabled

**application-prod.yml** - Production profile
- Database: Environment variables required
- Optimized connection pool: max 20 connections
- Minimal logging: INFO for application, WARN for security
- SQL logging disabled

**application-test.yml** - Testing profile
- In-memory H2 database (if configured)
- Fast startup for unit tests

**Switch Profile:**
```bash
# Via environment variable
export SPRING_PROFILES_ACTIVE=prod
./mvnw spring-boot:run

# Via command line
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Via application.yaml
spring.profiles.active=prod
```

### 11.5 Docker Deployment Options

**Option 1: Development Infrastructure Only (docker-compose-dev.yml)**

Best for local development with IDE or Maven:

```bash
docker-compose -f docker-compose-dev.yml up -d
```

**Services:**
- PostgreSQL: `localhost:5433` (note non-standard port)
- Redis: `localhost:6379`
- pgAdmin: `localhost:5050`

**Configuration Required:**
Update `application-dev.yml` to use port 5433:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/wasla
```

**Use Case:** Running app from IDE with hot reload, debugging, or testing

---

**Option 2: Full Stack Deployment (docker-compose.yml)**

Runs everything including the Spring Boot app:

```bash
docker-compose up --build
```

**Services:**
- PostgreSQL: Internal network (postgres:5432)
- Redis: Internal network (redis:6379)
- App: `localhost:8080`
- pgAdmin: `localhost:5050`

**Configuration:** Environment variables passed via docker-compose.yml

**Use Case:** Production-like environment, integration testing, demos

---

**Option 3: Production Deployment (Dockerfile.multistage)**

Multi-stage build for optimized production images:

```bash
# Build optimized image
docker build -f Dockerfile.multistage -t wasla-backend:latest .

# Run with external database
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/wasla \
  -e SPRING_DATASOURCE_USERNAME=wasla \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  -e JWT_SECRET=your-secret-key \
  wasla-backend:latest
```

**Benefits:**
- Smaller image size (JRE only, no build tools)
- Faster startup
- Security: Non-root user
- Layer caching for faster rebuilds

---

**Option 4: Windows Quick Start (start.bat)**

Convenience script for Windows developers:

```cmd
start.bat
```

**What it does:**
1. Starts full Docker stack (`docker-compose up -d`)
2. Waits 10 seconds for containers to initialize
3. Runs Spring Boot app with Maven

**Use Case:** Quick setup for Windows developers

---

### 11.6 Docker Container Management

**View Running Containers:**
```bash
# Dev infrastructure
docker-compose -f docker-compose-dev.yml ps

# Full stack
docker-compose ps
```

**View Logs:**
```bash
# All services
docker-compose -f docker-compose-dev.yml logs -f

# Specific service
docker-compose -f docker-compose-dev.yml logs -f postgres
docker-compose -f docker-compose-dev.yml logs -f redis
```

**Stop Containers:**
```bash
# Stop without removing volumes
docker-compose -f docker-compose-dev.yml stop

# Stop and remove containers (keeps volumes)
docker-compose -f docker-compose-dev.yml down

# Stop and remove everything including volumes (WARNING: deletes data)
docker-compose -f docker-compose-dev.yml down -v
```

**Restart Containers:**
```bash
docker-compose -f docker-compose-dev.yml restart postgres
```

**Access Container Shell:**
```bash
# PostgreSQL
docker exec -it wasla-postgres-dev psql -U wasla -d wasla

# Redis
docker exec -it wasla-redis-dev redis-cli

# App container (if using full stack)
docker exec -it wasla-app sh
```

**Check Container Health:**
```bash
docker inspect wasla-postgres-dev | grep -A 10 Health
```

### 11.7 API Testing Workflow

**1. Using Swagger UI:**
- Navigate to `http://localhost:8080/swagger-ui.html`
- Explore endpoints with interactive documentation
- Test requests directly from browser

**2. Using Postman:**

**Step 1: Register**
```http
POST http://localhost:8080/api/v1/auth/register/client
Content-Type: application/json

{
  "fullName": "Test Client",
  "email": "client@example.com",
  "phone": "+1234567890",
  "password": "Password123!"
}
```

**Step 2: Login**
```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "email": "client@example.com",
  "password": "Password123!"
}
```

**Step 3: Use Access Token from Response**
```http
POST http://localhost:8080/api/v1/jobs
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "pickupAddress": "123 Main St",
  "pickupLat": 40.7128,
  "pickupLng": -74.0060,
  "dropoffAddress": "456 Oak Ave",
  "dropoffLat": 40.7580,
  "dropoffLng": -73.9855,
  "cargoDesc": "Furniture"
}
```

### 11.8 WebSocket Testing

**Using JavaScript (Browser Console):**
```javascript
// Connect
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({
  'Authorization': 'Bearer <access_token>'
}, function(frame) {
  console.log('Connected:', frame);
  
  // Subscribe to location updates
  stompClient.subscribe('/topic/job/550e8400-e29b-41d4-a716-446655440000/location', 
    function(message) {
      console.log('Location update:', JSON.parse(message.body));
    }
  );
  
  // Send location update (as driver)
  stompClient.send('/app/driver.location', {}, JSON.stringify({
    lat: 40.7580,
    lng: -73.9855,
    jobId: '550e8400-e29b-41d4-a716-446655440000'
  }));
});
```

### 11.9 Debugging Tips

**Enable SQL Logging:**
```yaml
# application-dev.yml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

**View Generated Queries:**
```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

**Debug JWT Issues:**
```yaml
logging:
  level:
    com.example.wasla.auth: DEBUG
    org.springframework.security: DEBUG
```

**Common Issues:**

| Issue | Solution |
|-------|----------|
| Port 8080 already in use | Change `server.port` in application.yml or kill process |
| Database connection refused | Ensure Docker containers are running: `docker-compose ps` |
| JWT validation fails | Check `JWT_SECRET` matches between token generation and validation |
| OTP not found | OTP-based auth removed; use email/password to login |
| WebSocket connection fails | Ensure CORS is configured, check JWT token in handshake |

### 11.10 Code Style & Conventions

**Naming Conventions:**
- Classes: `PascalCase` (e.g., `JobService`)
- Methods: `camelCase` (e.g., `createJob`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_ATTEMPTS`)
- Packages: `lowercase` (e.g., `com.example.wasla.job`)

**Lombok Usage:**
- Entities: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- DTOs: `@Data` or `@Value` (immutable)
- Services: `@RequiredArgsConstructor` (constructor injection)

**Logging:**
```java
@Slf4j  // Lombok annotation
public class JobService {
    public void createJob() {
        log.info("Creating job for client: {}", clientId);
        log.debug("Job details: {}", jobDto);
        log.error("Failed to create job", exception);
    }
}
```

**Exception Handling:**
- Use custom exceptions: `BadRequestException`, `ResourceNotFoundException`
- Let `GlobalExceptionHandler` convert to API responses
- Log errors at appropriate level (warn for client errors, error for server errors)


---

## 13. Suggested Improvements

### 13.1 Architecture Improvements

**1. Implement Event-Driven Architecture**

**Current State:** Direct service-to-service calls for notifications

**Improvement:** Use Spring Events or message broker (RabbitMQ/Kafka)

```java
// Publish event
applicationEventPublisher.publishEvent(new JobCreatedEvent(job));

// Listen to event
@EventListener
public void onJobCreated(JobCreatedEvent event) {
    notificationService.notifyNearbyDrivers(event.getJob());
}
```

**Benefits:**
- Decoupling: Services don't directly depend on each other
- Scalability: Async processing, non-blocking
- Reliability: Event replay, dead letter queues
- Observability: Event audit trail

---

**2. Implement CQRS Pattern**

**Current State:** Same models for reads and writes

**Improvement:** Separate read models (queries) from write models (commands)

```java
// Write model (commands)
public class JobCommandService {
    public UUID createJob(CreateJobCommand cmd) { ... }
}

// Read model (queries)
public class JobQueryService {
    public JobListDto findNearbyJobs(NearbyJobsQuery query) { ... }
}
```

**Benefits:**
- Optimized read models (denormalized, cached)
- Independent scaling of reads vs writes
- Better performance for complex queries

---

**3. Add API Gateway Pattern**

**Current State:** Clients call backend directly

**Improvement:** Introduce API Gateway (Spring Cloud Gateway or Kong)

**Benefits:**
- Rate limiting per client
- Request/response transformation
- Centralized authentication
- Load balancing
- Circuit breaker pattern

---

### 13.2 Performance Improvements

**1. Implement Caching Strategy**

**Current State:** Every request hits database

**Improvement:** Use Redis for frequently accessed data

```java
@Cacheable(value = "drivers", key = "#id")
public Driver findById(UUID id) { ... }

@CacheEvict(value = "drivers", key = "#driver.id")
public Driver updateDriver(Driver driver) { ... }
```

**Cache Candidates:**
- Driver profiles (read-heavy)
- Job details (after creation)
- Nearby jobs (with TTL)

**Benefits:**
- Reduced database load
- Faster response times
- Better scalability

---

**2. Optimize Database Queries**

**Current Issues:**
- N+1 query problem with lazy loading
- Missing indexes on frequently queried columns

**Improvements:**

```java
// Use JOIN FETCH to avoid N+1
@Query("SELECT j FROM Job j JOIN FETCH j.client WHERE j.id = :id")
Optional<Job> findByIdWithClient(@Param("id") UUID id);

// Add composite indexes
CREATE INDEX idx_jobs_status_expires ON jobs(status, expires_at);
CREATE INDEX idx_bids_job_status ON bids(job_id, status);
```

**Benefits:**
- Fewer database round trips
- Faster query execution
- Reduced memory usage

---

**3. Implement Database Connection Pooling**

**Current State:** Default HikariCP settings

**Improvement:** Tune connection pool for production

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

---

**4. Add Pagination**

**Current State:** Returns all results (potential memory issues)

**Improvement:** Implement pagination for list endpoints

```java
@GetMapping("/my")
public Page<JobListDto> getMyJobs(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size) {
    return jobService.getMyJobs(PageRequest.of(page, size));
}
```

---

### 13.3 Security Improvements

**1. Implement Rate Limiting**

**Current State:** Only OTP requests are rate-limited

**Improvement:** Add rate limiting to all endpoints

```java
// Using Bucket4j or Spring Cloud Gateway
@RateLimiter(name = "jobCreation", fallbackMethod = "rateLimitFallback")
public JobResponseDto createJob(CreateJobDto dto) { ... }
```

**Benefits:**
- Prevent abuse and DDoS attacks
- Fair resource allocation
- Cost control (API gateway charges)

---

**2. Add Request Signing**

**Current State:** JWT only (can be stolen if intercepted)

**Improvement:** Implement HMAC request signing for sensitive operations

```java
// Client signs request with shared secret
String signature = HMAC_SHA256(requestBody + timestamp, clientSecret);

// Server validates signature
if (!signature.equals(expectedSignature)) {
    throw new UnauthorizedException("Invalid signature");
}
```

---

**3. Implement Audit Logging**

**Current State:** Basic application logs

**Improvement:** Structured audit trail for compliance

```java
@Aspect
public class AuditAspect {
    @AfterReturning("@annotation(Audited)")
    public void logAuditEvent(JoinPoint joinPoint) {
        auditRepository.save(AuditLog.builder()
            .userId(getCurrentUserId())
            .action(joinPoint.getSignature().getName())
            .timestamp(LocalDateTime.now())
            .build());
    }
}
```

**Track:**
- Job creation/modification
- Bid acceptance
- Status changes
- Profile updates

---

**4. Add Input Sanitization**

**Current State:** Basic validation only

**Improvement:** Sanitize user input to prevent XSS

```java
public String sanitize(String input) {
    return Jsoup.clean(input, Safelist.basic());
}
```

---

### 13.4 Maintainability Improvements

**1. Add Comprehensive Testing**

**Current State:** No tests (typical MVP)

**Improvement:** Implement test pyramid

```
Unit Tests (70%)
├── Service layer logic
├── Mapper transformations
└── Validation rules

Integration Tests (20%)
├── Repository queries
├── API endpoints
└── Security filters

E2E Tests (10%)
└── Complete user flows
```

**Example:**
```java
@SpringBootTest
@AutoConfigureMockMvc
class JobControllerTest {
    @Test
    void shouldCreateJob_whenValidRequest() {
        // Given
        String accessToken = authenticateAsClient();
        
        // When
        ResultActions result = mockMvc.perform(post("/api/v1/jobs")
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jobJson));
        
        // Then
        result.andExpect(status().isCreated())
              .andExpect(jsonPath("$.data.status").value("OPEN"));
    }
}
```

---

**2. Add API Versioning**

**Current State:** `/api/v1/...` (good start, but not enforced)

**Improvement:** Implement proper versioning strategy

```java
// URL versioning (current approach - keep it)
@RequestMapping("/api/v1/jobs")

// Or header versioning (alternative)
@GetMapping(headers = "API-Version=1")

// Or content negotiation
@GetMapping(produces = "application/vnd.wasla.v1+json")
```

**Benefits:**
- Backward compatibility
- Gradual migration
- Multiple versions in production

---

**3. Implement Feature Flags**

**Current State:** Features deployed all-or-nothing

**Improvement:** Use feature flags for gradual rollout

```java
@Service
public class JobService {
    @FeatureFlag("new-bidding-algorithm")
    public List<JobListDto> getNearbyJobs() {
        // New algorithm
    }
    
    public List<JobListDto> getNearbyJobsLegacy() {
        // Old algorithm (fallback)
    }
}
```

**Tools:** LaunchDarkly, Unleash, or custom implementation

---

**4. Add Health Checks**

**Current State:** Basic `/actuator/health`

**Improvement:** Custom health indicators

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        try {
            jobRepository.count();
            return Health.up().withDetail("database", "reachable").build();
        } catch (Exception e) {
            return Health.down().withException(e).build();
        }
    }
}
```

**Monitor:**
- Database connectivity
- Redis connectivity
- External API availability (FCM)
- Disk space
- Memory usage

---

**5. Implement Distributed Tracing**

**Current State:** Logs scattered across services

**Improvement:** Use Spring Cloud Sleuth + Zipkin

```yaml
spring:
  sleuth:
    sampler:
      probability: 1.0
  zipkin:
    base-url: http://localhost:9411
```

**Benefits:**
- Trace requests across services
- Identify performance bottlenecks
- Debug distributed systems

---

### 13.5 Scalability Improvements

**1. Implement Geospatial Queries (High Priority)**

**Current State:** Nearby jobs return all open jobs without distance filtering

**Improvement:** Implement PostGIS spatial queries

```java
@Query(value = """
    SELECT j.* FROM jobs j
    WHERE j.status = 'OPEN'
    AND ST_DWithin(
        ST_MakePoint(j.pickup_lng, j.pickup_lat)::geography,
        ST_MakePoint(:lng, :lat)::geography,
        :radiusMeters
    )
    ORDER BY ST_Distance(
        ST_MakePoint(j.pickup_lng, j.pickup_lat)::geography,
        ST_MakePoint(:lng, :lat)::geography
    )
    """, nativeQuery = true)
List<Job> findNearbyJobs(@Param("lat") BigDecimal lat, 
                         @Param("lng") BigDecimal lng, 
                         @Param("radiusMeters") double radiusMeters);
```

**Add Spatial Index:**
```sql
CREATE INDEX idx_jobs_pickup_location ON jobs 
USING GIST (ST_MakePoint(pickup_lng, pickup_lat)::geography);
```

**Benefits:**
- Accurate distance-based job discovery
- Reduced data transfer (only nearby jobs)
- Better driver experience

---

**2. Implement Horizontal Scaling**

**Current Issues:**
- Scheduled tasks run on all instances (duplicate work)
- WebSocket connections tied to single instance

**Solutions:**

**Distributed Locking (for schedulers):**
```java
@Scheduled(fixedRate = 60_000)
@SchedulerLock(name = "expireJobs", lockAtMostFor = "50s")
public void expireJobs() { ... }
```

**Redis-backed STOMP Relay (for WebSocket):**
```yaml
spring:
  websocket:
    stomp:
      relay:
        host: localhost
        port: 61613
```

---

**2. Implement Database Sharding**

**Current State:** Single PostgreSQL instance

**Future:** Shard by geography or user ID

```
US-East DB: Users with ID hash % 3 == 0
US-West DB: Users with ID hash % 3 == 1
EU DB:      Users with ID hash % 3 == 2
```

---

**3. Add CDN for Static Assets**

**Current State:** Cargo photos stored as URLs (external)

**Improvement:** Use S3 + CloudFront for image hosting

```java
public String uploadCargoPhoto(MultipartFile file) {
    String key = "cargo-photos/" + UUID.randomUUID() + ".jpg";
    s3Client.putObject(bucket, key, file.getInputStream());
    return cloudFrontUrl + "/" + key;
}
```

---

### 13.6 Operational Improvements

**1. Add Monitoring & Alerting**

**Tools:**
- **Prometheus** + **Grafana**: Metrics visualization
- **ELK Stack**: Log aggregation
- **Sentry**: Error tracking
- **PagerDuty**: On-call alerting

**Key Metrics:**
- Request rate, latency, error rate (RED metrics)
- Database connection pool usage
- JVM memory/GC metrics
- Business metrics (jobs created, bids submitted)

---

**2. Implement CI/CD Pipeline**

**Current State:** Manual deployment

**Improvement:** Automated pipeline

```yaml
# .github/workflows/deploy.yml
name: Deploy
on:
  push:
    branches: [main]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Build
        run: ./mvnw clean package
      - name: Run tests
        run: ./mvnw test
      - name: Build Docker image
        run: docker build -t movemate-backend .
      - name: Push to registry
        run: docker push movemate-backend
      - name: Deploy to production
        run: kubectl apply -f k8s/
```

---

**3. Add Backup & Disaster Recovery**

**Current State:** No backup strategy

**Improvement:**
- Automated daily PostgreSQL backups
- Point-in-time recovery (PITR)
- Multi-region replication
- Backup testing (restore drills)

---

**5. Implement Push Notification Integration (High Priority)**

**Current State:** FCM notifications logged to console only

**Improvement:** Complete Firebase Cloud Messaging integration

```java
// Add Firebase Admin SDK dependency (already in pom.xml)
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.4.2</version>
</dependency>

// Initialize Firebase
@PostConstruct
public void initializeFirebase() throws IOException {
    FileInputStream serviceAccount = 
        new FileInputStream("src/main/resources/firebase-service-account.json");
    
    FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();
    
    FirebaseApp.initializeApp(options);
}

// Send push notification
public void sendPushNotification(String fcmToken, String title, String body, Map<String, String> data) {
    Message message = Message.builder()
        .setToken(fcmToken)
        .setNotification(Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build())
        .putAllData(data)  // For deep linking
        .build();
    
    try {
        String response = FirebaseMessaging.getInstance().send(message);
        log.info("Successfully sent message: {}", response);
    } catch (FirebaseMessagingException e) {
        log.error("Failed to send FCM notification", e);
    }
}
```

**Setup Steps:**
1. Create Firebase project at https://console.firebase.google.com
2. Download service account JSON key
3. Add to `src/main/resources/firebase-service-account.json`
4. Update `NotificationService` to use Firebase SDK
5. Test with mobile app FCM tokens

---

**6. Implement SMS Gateway Integration**

---

## 12. Troubleshooting & Common Issues

### 12.1 Authentication Issues

**Issue: JWT token expires too quickly**
- **Solution:** Access tokens expire after 7 days (604,800,000 ms). Use the refresh token endpoint to get a new access token, or re-login.
- **Configuration:** `jwt.access-token-expiration` in `application.yaml`

**Issue: "Invalid or expired token" error**
- **Cause:** Token has expired or JWT secret mismatch
- **Solution:** 
  - Login again to get fresh tokens
  - Use the refresh token endpoint: `POST /api/v1/auth/refresh`
  - Ensure `JWT_SECRET` environment variable is consistent across restarts
  - Check token expiry time in JWT payload

**Issue: "Password authentication failed"**
- **Cause:** Wrong email/password combination or database mismatch
- **Solution:** 
  - Verify email and password are correct
  - Check that the user has been registered

---

### 12.2 Contact Information Issues

**Issue: Driver/Client name shows as "null" or "Name not set"**
- **Cause:** Users may not have set their name during registration or profile update
- **Solution:** 
  - Users must update their profile via mobile app
  - Drivers: Use "Edit Profile" in profile screen
  - Clients: Use "Edit Profile" in profile screen
  - API: `PUT /api/v1/drivers/me/profile` or `PUT /api/v1/clients/me/profile`

**Issue: Contact information not visible after bid acceptance**
- **Cause:** Job status must be `CONFIRMED` for contact info to appear
- **Solution:** 
  - Verify job status is `CONFIRMED` (not `BIDDING` or `OPEN`)
  - Check API response includes `driverPhone` or `clientPhone` field
  - Mobile app shows contact card only when phone number is present

**Issue: Phone number format incorrect**
- **Cause:** Missing country code
- **Solution:** Always include country code (e.g., `+20` for Egypt, `+1` for US)
- **Example:** `+201062315558` (Egypt), `+11234567890` (US)

---

### 12.3 Rating Issues

**Issue: "Score is required" validation error**
- **Cause:** Using field name `rating` instead of `score`
- **Solution:** Use `score` field in request body:
  ```json
  {
    "score": 5,
    "comment": "Great service!"
  }
  ```

**Issue: Rating submission fails with 400 error**
- **Cause:** Invalid score value (must be 1-5)
- **Solution:** Ensure score is between 1 and 5 (inclusive)

**Issue: Cannot submit rating**
- **Cause:** Job must be in `COMPLETED` status
- **Solution:** Driver must mark job as completed first

---

### 12.4 Job Status Issues

**Issue: Job stuck in `BIDDING` status**
- **Cause:** Client hasn't accepted any bid yet
- **Solution:** Client must accept a bid via `PATCH /api/v1/jobs/{jobId}/bids/{bidId}/accept`

**Issue: Job shows `EXPIRED` status**
- **Cause:** No bid was accepted within 30 minutes of job creation
- **Solution:** Jobs automatically expire after 30 minutes. Create a new job.

**Issue: Cannot update job status**
- **Cause:** Invalid status transition
- **Solution:** Follow valid status flow:
  - `OPEN` → `BIDDING` (automatic when first bid submitted)
  - `BIDDING` → `CONFIRMED` (when client accepts bid)
  - `CONFIRMED` → `IN_PROGRESS` (driver starts job)
  - `IN_PROGRESS` → `COMPLETED` (driver completes job)

---

### 12.5 Database Issues

**Issue: Database connection refused**
- **Cause:** PostgreSQL container not running or wrong port
- **Solution:** 
  ```bash
  # Check container status
  docker-compose -f docker-compose-dev.yml ps
  
  # Start containers
  docker-compose -f docker-compose-dev.yml up -d
  
  # Check logs
  docker-compose -f docker-compose-dev.yml logs postgres
  ```
- **Port Configuration:**
  - Dev mode (`docker-compose-dev.yml`): Port 5433 → Update `application-dev.yml` to use `localhost:5433`
  - Production mode (`docker-compose.yml`): Port 5432 → App container uses `postgres:5432`

**Issue: "Password authentication failed for user wasla"**
- **Cause:** Incorrect credentials or stale Docker volumes
- **Solution:** 
  - Verify credentials match between docker-compose and application config
  - Dev: username=wasla, password=secret (from docker-compose-dev.yml)
  - Reset volumes: `docker-compose -f docker-compose-dev.yml down -v && docker-compose -f docker-compose-dev.yml up -d`

**Issue: Migration fails**
- **Cause:** Schema mismatch or dirty migration state
- **Solution:**
  ```bash
  # Reset database (WARNING: deletes all data)
  docker-compose -f docker-compose-dev.yml down -v
  docker-compose -f docker-compose-dev.yml up -d
  ```

**Issue: "relation does not exist" error**
- **Cause:** Migrations haven't run
- **Solution:** 
  ```bash
  ./mvnw flyway:migrate
  ```

---

### 12.6 WebSocket Issues

**Issue: WebSocket connection fails**
- **Cause:** CORS configuration or missing JWT token
- **Solution:**
  - Ensure JWT token is passed in WebSocket handshake
  - Check CORS configuration allows WebSocket connections
  - Verify SockJS fallback is enabled

**Issue: Location updates not received**
- **Cause:** Not subscribed to correct topic
- **Solution:** Subscribe to `/topic/job/{jobId}/location` for specific job

---

### 12.7 Mobile App Issues

**Issue: Navigation doesn't work after login**
- **Cause:** Async timing issue
- **Solution:** Navigation should happen immediately after successful login/registration

**Issue: Profile changes not reflected**
- **Cause:** State not refreshed after update
- **Solution:** Mobile app automatically refreshes profile after updates

**Issue: Contact information not showing**
- **Cause:** Missing name in user profile or wrong job status
- **Solution:**
  - Update profile with name
  - Verify job status is `CONFIRMED` or later
  - Check that phone number is present in API response

---

### 12.8 Performance Issues

**Issue: Slow API responses**
- **Cause:** Database queries without indexes or N+1 query problem
- **Solution:**
  - Check slow query logs
  - Add indexes on frequently queried columns
  - Use JOIN FETCH to avoid N+1 queries

**Issue: High memory usage**
- **Cause:** Loading too many entities at once
- **Solution:** Implement pagination for list endpoints

---

### 12.9 Configuration Issues

**Issue: Application won't start**
- **Cause:** Missing environment variables or port conflict
- **Solution:**
  - Check required environment variables (JWT_SECRET, database credentials)
  - Verify port 8080 is available
  - Check application logs for specific error

**Issue: Wrong profile active**
- **Cause:** `SPRING_PROFILES_ACTIVE` not set
- **Solution:**
  ```bash
  export SPRING_PROFILES_ACTIVE=dev
  ./mvnw spring-boot:run
  ```

---

## Conclusion

Wasla is a well-structured logistics marketplace platform built with modern Spring Boot practices. The codebase demonstrates:

✅ **Clear separation of concerns** (layered architecture)  
✅ **Secure authentication** (JWT + Email/Password + Refresh Token Rotation)  
✅ **Role-based authorization** (Client vs Driver)  
✅ **Real-time capabilities** (WebSocket location tracking)  
✅ **Scalable design** (stateless, PostgreSQL + Redis)  
✅ **Production-ready patterns** (MapStruct DTOs, global exception handling)  
✅ **Docker infrastructure** (dev and production compose files with health checks)  
✅ **Database migrations** (Flyway with UUID-based schema)  
✅ **Connection pooling** (HikariCP with optimized settings)  

**Current MVP Status:**
- ✅ Core job lifecycle (create, bid, accept, complete, rate)
- ✅ Authentication and authorization
- ✅ Real-time location tracking via WebSocket
- ⏳ Geospatial queries (PostGIS available but not implemented)
- ⏳ Push notifications (FCM stub, integration pending)

**Priority Improvements:**
1. Implement PostGIS spatial queries for accurate nearby job discovery
2. Complete Firebase Cloud Messaging integration
3. Add comprehensive test coverage
4. Implement distributed locking for scheduled tasks

The suggested improvements provide a roadmap for evolving from MVP to production-scale system, focusing on performance, security, and operational excellence.

---

**Document Version:** 3.1  
**Last Updated:** March 27, 2026  
**Author:** Technical Documentation Team  
**Target Audience:** Backend developers, system architects, DevOps engineers

**Recent Changes (v3.1):**
- Clarified PostGIS status: Extension available but spatial queries not yet implemented
- Updated notification service status: Stub implementation, FCM integration pending
- Added detailed Docker configuration differences (dev vs prod)
- Documented start.bat Windows convenience script
- Corrected database port configurations (5433 for dev, 5432 for prod)
- Added HikariCP connection pool configuration details
- Updated infrastructure versions (PostGIS 16-3.4, Redis 7 Alpine, Eclipse Temurin 17)
- Clarified nearby jobs implementation (currently returns all open jobs)
- Documented two-stage migration process (V1 init, V2 UUID refactor)
- Added Docker container management commands
- Expanded deployment options documentation
- Added application startup verification section


---

## 14. Appendix

### 14.1 Quick Reference - Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | Yes (prod) | `jdbc:postgresql://localhost:5432/wasla` | Database connection URL |
| `SPRING_DATASOURCE_USERNAME` | Yes (prod) | `wasla` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Yes (prod) | `secret` | Database password |
| `SPRING_DATA_REDIS_HOST` | Yes (prod) | `localhost` | Redis host |
| `SPRING_DATA_REDIS_PORT` | No | `6379` | Redis port |
| `JWT_SECRET` | Yes | Dev default | JWT signing secret (256-bit minimum) |
| `SPRING_PROFILES_ACTIVE` | No | None | Active profile (dev, prod, test) |

### 14.2 Quick Reference - Docker Ports

**Development Mode (docker-compose-dev.yml):**
| Service | Host Port | Container Port | Access |
|---------|-----------|----------------|--------|
| PostgreSQL | 5433 | 5432 | `localhost:5433` |
| Redis | 6379 | 6379 | `localhost:6379` |
| pgAdmin | 5050 | 80 | `http://localhost:5050` |

**Production Mode (docker-compose.yml):**
| Service | Host Port | Container Port | Access |
|---------|-----------|----------------|--------|
| PostgreSQL | 5432 | 5432 | Internal only |
| Redis | 6379 | 6379 | Internal only |
| App | 8080 | 8080 | `http://localhost:8080` |
| pgAdmin | 5050 | 80 | `http://localhost:5050` |

### 14.3 Quick Reference - API Endpoints Summary

**Authentication (Public):**
- `POST /api/v1/auth/register/client` - Register client
- `POST /api/v1/auth/register/driver` - Register driver
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token
- `POST /api/v1/auth/logout` - Logout

**Jobs (Authenticated):**
- `POST /api/v1/jobs` - Create job (CLIENT)
- `GET /api/v1/jobs/{id}` - Get job details
- `GET /api/v1/jobs/my` - Get my jobs (paginated)
- `GET /api/v1/jobs/nearby` - Find nearby jobs (DRIVER)
- `PATCH /api/v1/jobs/{id}/status` - Update job status (DRIVER)

**Bids (Authenticated):**
- `POST /api/v1/jobs/{jobId}/bids` - Submit bid (DRIVER)
- `GET /api/v1/jobs/{jobId}/bids` - Get all bids
- `PATCH /api/v1/jobs/{jobId}/bids/{bidId}/accept` - Accept bid (CLIENT)

**Ratings (CLIENT):**
- `POST /api/v1/jobs/{jobId}/rating` - Submit rating
- `GET /api/v1/jobs/{jobId}/rating` - Get rating

**Client Profile (CLIENT):**
- `GET /api/v1/clients/me` - Get profile
- `PUT /api/v1/clients/me/profile` - Update profile
- `PUT /api/v1/clients/me/fcm-token` - Update FCM token

**Driver Profile (DRIVER):**
- `GET /api/v1/drivers/me` - Get profile
- `PUT /api/v1/drivers/me/profile` - Update profile
- `PATCH /api/v1/drivers/me/status` - Update availability
- `PUT /api/v1/drivers/me/location` - Update GPS location
- `PUT /api/v1/drivers/me/fcm-token` - Update FCM token
- `GET /api/v1/drivers/me/bids` - Get my bids
- `GET /api/v1/drivers/{id}` - Get public profile (any authenticated user)

**WebSocket:**
- `ws://localhost:8080/ws` - WebSocket connection
- `/app/driver.location` - Send location update (DRIVER)
- `/topic/job/{jobId}/location` - Subscribe to location updates

### 14.4 Quick Reference - Database Schema

**Tables:**
- `clients` - Client profiles and authentication
- `drivers` - Driver profiles, vehicle info, and location
- `jobs` - Move requests with pickup/dropoff locations
- `bids` - Driver price quotes on jobs
- `ratings` - Client feedback on completed jobs
- `refresh_tokens` - Persistent refresh tokens for session management

**Key Relationships:**
- Client → Jobs (one-to-many)
- Driver → Jobs (one-to-many, nullable)
- Job → Bids (one-to-many)
- Job → Rating (one-to-one)
- Driver → Bids (one-to-many)

### 14.5 Quick Reference - Job Status Flow

```
OPEN → BIDDING → CONFIRMED → IN_PROGRESS → COMPLETED
  ↓
EXPIRED (after 30 minutes if no bid accepted)
```

**Status Transitions:**
- `OPEN` → `BIDDING`: Automatic when first bid submitted
- `BIDDING` → `CONFIRMED`: Client accepts a bid
- `CONFIRMED` → `IN_PROGRESS`: Driver starts job
- `IN_PROGRESS` → `COMPLETED`: Driver completes job
- `OPEN/BIDDING` → `EXPIRED`: Automatic after 30 minutes (scheduler)

### 14.6 Quick Reference - Bid Status Flow

```
PENDING → ACCEPTED (winning bid)
       → WITHDRAWN (other bids when one is accepted)
```

### 14.7 Quick Reference - Common Commands

**Start Development:**
```bash
# Start infrastructure
docker-compose -f docker-compose-dev.yml up -d

# Run application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Stop Everything:**
```bash
# Stop app (Ctrl+C in terminal)

# Stop infrastructure
docker-compose -f docker-compose-dev.yml down
```

**Reset Database:**
```bash
docker-compose -f docker-compose-dev.yml down -v
docker-compose -f docker-compose-dev.yml up -d
```

**View Logs:**
```bash
# Application logs (in console where app is running)

# Database logs
docker-compose -f docker-compose-dev.yml logs -f postgres

# All container logs
docker-compose -f docker-compose-dev.yml logs -f
```

**Build Production Image:**
```bash
# Standard build
docker build -t wasla-backend:latest .

# Optimized multi-stage build
docker build -f Dockerfile.multistage -t wasla-backend:latest .
```

### 14.8 Quick Reference - Technology Versions

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming language |
| Spring Boot | 3.3.3 | Application framework |
| PostgreSQL | 16 | Database |
| PostGIS | 3.4 | Geospatial extension |
| Redis | 7 Alpine | Caching |
| JJWT | 0.13.0 | JWT library |
| MapStruct | 1.5.5.Final | Entity-DTO mapping |
| SpringDoc OpenAPI | 2.6.0 | API documentation |
| Eclipse Temurin | 17 JRE/JDK | Docker base image |

### 14.9 Quick Reference - Default Credentials

**Development Database (docker-compose-dev.yml):**
- Host: `localhost:5433`
- Database: `wasla`
- Username: `wasla`
- Password: `secret`

**Production Database (docker-compose.yml):**
- Host: `postgres:5432` (internal)
- Database: `wasla`
- Username: `wasla`
- Password: `secret`

**pgAdmin:**
- URL: `http://localhost:5050`
- Email: `admin@wasla.app`
- Password: `admin`

**JWT Secret (Development):**
```
waslaDefaultSecretKeyForDevelopmentOnly2026MustBeAtLeast256Bits
```

⚠️ **WARNING:** Change all default credentials in production!

### 14.10 Quick Reference - Project Structure

```
wasla/
├── src/main/java/com/example/wasla/
│   ├── auth/                    # Authentication & JWT
│   ├── common/                  # Shared utilities & exceptions
│   ├── config/                  # Spring configuration
│   ├── job/                     # Core business logic
│   ├── location/                # Real-time tracking
│   ├── notification/            # Push notifications (stub)
│   ├── rating/                  # Driver ratings
│   └── user/                    # Client & Driver management
├── src/main/resources/
│   ├── db/migration/            # Flyway migrations
│   ├── application.yaml         # Base config
│   ├── application-dev.yml      # Dev config
│   └── application-prod.yml     # Prod config
├── docker-compose-dev.yml       # Dev infrastructure
├── docker-compose.yml           # Full stack
├── Dockerfile                   # Simple build
├── Dockerfile.multistage        # Optimized build
├── start.bat                    # Windows quick start
└── pom.xml                      # Maven dependencies
```

---

**End of Documentation**
