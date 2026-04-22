<div align="center">

# 🚛 Wasla

### Smart Logistics Marketplace Platform

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![PostGIS](https://img.shields.io/badge/PostGIS-3.4-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://postgis.net/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

**A modern two-sided marketplace connecting clients with truck drivers through competitive bidding**

[Features](#-features) • [Quick Start](#-quick-start) • [API Docs](#-api-documentation) • [Architecture](#-architecture) • [Contributing](#-contributing)

</div>

---

## 📖 Overview

Wasla is a **logistics marketplace platform** that revolutionizes the moving and transportation industry by connecting clients directly with independent truck drivers through a transparent bidding system.

### 🎯 The Problem

- **Clients** struggle to find reliable transport at fair prices
- **Drivers** miss job opportunities due to lack of centralized platforms
- **Middlemen** inflate costs and reduce transparency

### ✨ The Solution

Wasla eliminates inefficiencies by providing:

- 🤝 **Direct Connection** - No middlemen between clients and drivers
- 💰 **Competitive Bidding** - Market-driven pricing ensures fairness
- 📍 **Real-time Tracking** - Live GPS updates via WebSocket
- 🗺️ **Geospatial Matching** - PostGIS-powered nearby job/driver discovery
- ⭐ **Trust System** - Rating mechanism ensures quality service
- 🔔 **Smart Notifications** - FCM push notifications for all events

---

## 🌟 Features

<table>
<tr>
<td width="50%">

### 👤 For Clients

- ✅ Post move requests with pickup/dropoff locations
- ✅ Receive competitive bids from nearby drivers
- ✅ Compare driver ratings and prices
- ✅ Track driver location in real-time
- ✅ Rate drivers after job completion
- ✅ View complete job history
- ✅ Find available drivers nearby

</td>
<td width="50%">

### 🚛 For Drivers

- ✅ Discover nearby jobs automatically (PostGIS ST_DWithin)
- ✅ Submit competitive price bids
- ✅ Manage job status (start, complete)
- ✅ Update GPS location in real-time
- ✅ Build reputation through ratings
- ✅ Toggle availability status
- ✅ Track earnings and completed jobs

</td>
</tr>
</table>

### 🔥 Technical Highlights

- 🔐 **JWT Authentication** (HS384) with refresh token rotation
- 🗄️ **PostgreSQL + PostGIS** for geospatial queries (ST_DWithin)
- 🚀 **Redis Caching** for performance optimization
- 📊 **Flyway Migrations** for database version control
- 🗺️ **MapStruct** for compile-time DTO mapping
- 📖 **OpenAPI/Swagger** for interactive API documentation
- 🔌 **WebSocket (STOMP)** for real-time communication
- ⏰ **Scheduled Tasks** for automatic job expiry
- 🐳 **Docker Support** with multi-stage builds
- 🔔 **Firebase Cloud Messaging** for push notifications
- 📍 **Location History** with route tracking

---

## 🛠️ Technology Stack

<table>
<tr>
<td>

**Backend Framework**
- Java 21 (LTS)
- Spring Boot 3.4
- Spring Security
- Spring Data JPA
- Spring WebSocket
- Maven

</td>
<td>

**Database & Cache**
- PostgreSQL 16
- PostGIS 3.4 (Spatial)
- Redis 7
- Flyway
- HikariCP

</td>
<td>

**Tools & Libraries**
- Lombok
- MapStruct 1.5.5
- JJWT 0.13.0
- SpringDoc OpenAPI
- Firebase Admin SDK
- Docker & Docker Compose

</td>
</tr>
</table>

---

## 🚀 Quick Start

### Prerequisites

```bash
☕ Java 21+
🐳 Docker & Docker Compose
📦 Maven 3.6+
```

### Option 1: Docker (Recommended)

```bash
# Clone the repository
git clone https://github.com/alaa-333/Wasla.git
cd Wasla/wasla-backend/wasla

# Start infrastructure (PostgreSQL, Redis, pgAdmin)
docker-compose -f docker-compose-dev.yml up -d

# Run the application
./mvnw spring-boot:run
```

### Option 2: Full Docker Stack

```bash
# Run everything in Docker (app + infrastructure)
docker-compose up -d
```

### Option 3: Windows Quick Start

```bash
# Use the convenience script
start.bat
```

### 🌐 Access Points

| Service | URL | Credentials |
|---------|-----|-------------|
| **API** | http://localhost:8080 | - |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | - |
| **pgAdmin** | http://localhost:5050 | admin@wasla.com / admin |
| **PostgreSQL** | localhost:5433 (dev) | wasla / secret |

---

## 📚 API Documentation

### Interactive Documentation

Visit **[Swagger UI](http://localhost:8080/swagger-ui.html)** for complete interactive API documentation.

### Core Endpoints

<details>
<summary><b>🔐 Authentication</b></summary>

```http
POST   /api/v1/auth/register/client    # Register as client
POST   /api/v1/auth/register/driver    # Register as driver
POST   /api/v1/auth/login              # Login (returns JWT)
POST   /api/v1/auth/refresh            # Refresh access token
POST   /api/v1/auth/logout             # Logout (invalidate token)
```

**Example Request:**
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
  "data": {
    "accessToken": "eyJhbGciOiJIUzM4...",
    "refreshToken": "dO9y28nfG_2...",
    "expiresIn": 604800,
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "email": "user@example.com",
      "role": "CLIENT"
    }
  }
}
```

</details>

<details>
<summary><b>📦 Jobs Management</b></summary>

```http
POST   /api/v1/jobs              # Create job (CLIENT)
GET    /api/v1/jobs/{id}         # Get job details
GET    /api/v1/jobs/my           # Get my jobs (paginated)
GET    /api/v1/jobs/nearby       # Find nearby jobs (DRIVER) - PostGIS
PATCH  /api/v1/jobs/{id}/status  # Update job status (DRIVER)
```

</details>

<details>
<summary><b>💰 Bidding System</b></summary>

```http
POST   /api/v1/jobs/{jobId}/bids           # Submit bid (DRIVER)
GET    /api/v1/jobs/{jobId}/bids           # Get bids for job (CLIENT)
PATCH  /api/v1/jobs/{jobId}/bids/{bidId}/accept  # Accept bid (CLIENT)
```

</details>

<details>
<summary><b>🗺️ Spatial Queries (PostGIS)</b></summary>

```http
GET    /api/v1/spatial/jobs/nearby          # Find jobs within radius (DRIVER)
GET    /api/v1/spatial/jobs/nearby/count    # Count jobs within radius
GET    /api/v1/spatial/drivers/nearby       # Find drivers within radius (CLIENT)
GET    /api/v1/spatial/drivers/nearby/count # Count drivers within radius
GET    /api/v1/spatial/analytics/coverage   # Multi-radius coverage analytics
GET    /api/v1/spatial/distance             # Calculate distance between points
```

**Example: Find nearby jobs**
```
GET /api/v1/spatial/jobs/nearby?lat=30.0444&lng=31.2357&radiusKm=15
```

</details>

<details>
<summary><b>📍 Location History</b></summary>

```http
GET    /api/v1/locations/jobs/{jobId}/route   # Get complete route history
GET    /api/v1/locations/jobs/{jobId}/latest  # Get latest GPS position
```

</details>

<details>
<summary><b>⭐ Ratings</b></summary>

```http
POST   /api/v1/jobs/{jobId}/rating   # Rate driver (CLIENT)
GET    /api/v1/jobs/{jobId}/rating   # Get rating for job
```

</details>

<details>
<summary><b>👤 Profile Management</b></summary>

```http
GET    /api/v1/clients/me             # Get my profile (CLIENT)
PUT    /api/v1/clients/me/profile     # Update profile (CLIENT)
PUT    /api/v1/clients/me/fcm-token   # Update FCM token (CLIENT)

GET    /api/v1/drivers/me             # Get my profile (DRIVER)
PUT    /api/v1/drivers/me/profile     # Update profile (DRIVER)
PUT    /api/v1/drivers/me/status      # Update availability (DRIVER)
PUT    /api/v1/drivers/me/location    # Update GPS location (DRIVER)
PUT    /api/v1/drivers/me/fcm-token   # Update FCM token (DRIVER)
GET    /api/v1/drivers/me/bids        # Get my submitted bids (DRIVER)
GET    /api/v1/drivers/{id}           # Get public driver profile
```

</details>

### Job Lifecycle

```
OPEN → BIDDING → CONFIRMED → IN_PROGRESS → COMPLETED
  ↓
EXPIRED (if no bid accepted within 30 minutes)
```

---

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────────────────────────────────────────────────┐
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
│  │  + PostGIS   │  │              │  │     FCM      │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

### Package Structure

```
com.example.wasla/
├── auth/               # Authentication & JWT
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   ├── security/
│   └── AuthController.java, AuthService.java
├── common/             # Shared components
│   ├── dto/           # ApiResponse, ApiError, Pagination
│   ├── entity/        # BaseEntity
│   └── exception/     # Global exception handling
├── config/            # Spring configuration
│   ├── SecurityConfig.java
│   ├── WebSocketConfig.java
│   ├── FirebaseConfig.java
│   └── BeanConfiguration.java
├── job/               # Job & bidding domain
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── mapper/
│   ├── repository/
│   └── service/
├── location/          # Real-time GPS tracking & spatial queries
│   ├── controller/
│   ├── dto/
│   ├── service/
│   ├── LocationHistory.java
│   └── LocationService.java
├── notification/      # Push notifications (FCM)
│   ├── controller/
│   ├── dto/
│   └── service/
├── rating/            # Rating system
│   ├── RatingController.java
│   ├── RatingService.java
│   └── Rating.java
└── user/              # User management
    ├── client/
    └── driver/
```

---

## 🗄️ Database Schema

### Entity Relationship Diagram

```
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   CLIENTS    │         │    JOBS      │         │   DRIVERS    │
├──────────────┤         ├──────────────┤         ├──────────────┤
│ id (UUID)    │◄───────►│ id (UUID)    │◄───────►│ id (UUID)    │
│ email        │    1:N  │ client_id    │  N:1    │ email        │
│ password     │         │ driver_id    │         │ vehicle_type │
│ phone        │         │ status       │         │ rating_avg   │
│ fcm_token    │         │ pickup_*     │         │ is_available │
└──────────────┘         │ dropoff_*    │         │ current_lat  │
                         │ accepted_price│        │ current_lng  │
                         └──────┬───────┘         │ fcm_token    │
                                │                 └──────────────┘
                         ┌──────┴───────┐
                         │              │
                    ┌────▼────┐   ┌─────▼────┐   ┌──────────────┐
                    │  BIDS   │   │ RATINGS  │   │ LOCATION_    │
                    ├─────────┤   ├──────────┤   │ HISTORY      │
                    │ job_id  │   │ job_id   │   ├──────────────┤
                    │ driver_id│  │ score    │   │ job_id       │
                    │ price   │   │ comment  │   │ driver_id    │
                    │ status  │   └──────────┘   │ lat, lng     │
                    └─────────┘                  │ recorded_at  │
                                                └──────────────┘
```

**See [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md) for complete documentation.**

---


### Application Profiles

```bash
# Development
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Production
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Testing
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

---

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Run specific test
./mvnw test -Dtest=AuthServiceTest
```

---

## 🐳 Docker Deployment

### Build Image

```bash
# Standard build
docker build -t wasla-backend:latest .

# Multi-stage optimized build
docker build -f Dockerfile.multistage -t wasla-backend:latest .
```

### Deploy with Docker Compose

```bash
# Development (infrastructure only)
docker-compose -f docker-compose-dev.yml up -d

# Production (full stack)
docker-compose up -d
```

---

## 🔒 Security

- ✅ JWT authentication (HS384) with refresh token rotation
- ✅ BCrypt password hashing
- ✅ Role-based access control (RBAC)
- ✅ SQL injection prevention (prepared statements)
- ✅ CORS configuration
- ✅ Input validation with Bean Validation
- ✅ Transaction isolation (SERIALIZABLE for bid acceptance)
- ✅ Phone number visibility restricted to confirmed jobs

---

## 🗺️ PostGIS Spatial Queries

Wasla uses PostGIS for accurate geospatial queries:

### Find Nearby Jobs (ST_DWithin)

```sql
SELECT j.*, 
       ST_Distance(
         ST_MakePoint(j.pickup_lng, j.pickup_lat)::geography,
         ST_MakePoint(:driverLng, :driverLat)::geography
       ) AS distance_meters
FROM jobs j
WHERE j.status IN ('OPEN', 'BIDDING')
  AND j.expires_at > NOW()
  AND ST_DWithin(
        ST_MakePoint(j.pickup_lng, j.pickup_lat)::geography,
        ST_MakePoint(:driverLng, :driverLat)::geography,
        15000  -- 15km radius
      )
ORDER BY distance_meters ASC;
```

### Find Available Drivers Nearby

```sql
SELECT d.*,
       ST_Distance(
         ST_MakePoint(d.current_lng, d.current_lat)::geography,
         ST_MakePoint(:lng, :lat)::geography
       ) AS distance_meters
FROM drivers d
WHERE d.is_available = TRUE
  AND d.current_lat IS NOT NULL
  AND ST_DWithin(
        ST_MakePoint(d.current_lng, d.current_lat)::geography,
        ST_MakePoint(:lng, :lat)::geography,
        10000  -- 10km radius
      )
ORDER BY distance_meters ASC;
```

---

## 🔔 FCM Notifications

Firebase Cloud Messaging is fully integrated for push notifications:

| Event | Recipient | Trigger |
|-------|-----------|---------|
| `JOB_POSTED` | Nearby drivers | Job created |
| `BID_PLACED` | Job owner (client) | Driver submits bid |
| `BID_ACCEPTED` | Winning driver | Client accepts bid |
| `JOB_STARTED` | Client | Driver starts job |
| `JOB_COMPLETED` | Client | Driver completes job |
| `DRIVER_RATED` | Driver | Client submits rating |

---

## 📋 Roadmap

- [x] Core authentication system
- [x] Job creation and bidding
- [x] Real-time location tracking (WebSocket)
- [x] Rating system
- [x] Docker support
- [x] PostGIS spatial queries (ST_DWithin)
- [x] Firebase Cloud Messaging
- [x] Location history & route tracking
- [ ] Payment integration
- [ ] Mobile app (Flutter)
- [ ] Admin dashboard
- [ ] Analytics and reporting

---


## 📝 Documentation

- 📖 [API Documentation](http://localhost:8080/swagger-ui.html)
- 🗄️ [Database Schema](DATABASE_SCHEMA.md)


---

---

## 👥 Team

<table>
<tr>
<td align="center">
<a href="https://github.com/alaa-333">
<img src="https://github.com/alaa-333.png" width="100px;" alt="Alaa"/><br />
<sub><b>Alaa</b></sub>
</a><br />
<sub>Backend Developer</sub>
</td>
</tr>
</table>

---



<div align="center">

**⭐ Star this repo if you find it helpful!**

[⬆ Back to Top](#-wasla)

</div>
