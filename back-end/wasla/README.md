# 🚛 Wasla - Smart Logistics Marketplace

> **Wasla** is a full-stack logistics management system that connects clients who need transportation services with available truck owners.  
> Built with **Spring Boot** for the Backend and **Flutter** for mobile applications.

---

## 🚀 About the Project

### 📋 The Problem
Finding reliable transport vehicles at transparent prices is difficult, and drivers lose job opportunities due to the absence of a centralized platform.

### ✅ The Solution
A smart platform that provides:
- 📍 Live order tracking system
- 🧮 Automatic cost calculation based on distance and weight using Google Maps API
- 📱 Dedicated interfaces for both clients and drivers

---

## 🛠 Tech Stack

### Backend

| Technology | Usage |
|-----------|-------|
| Java 17 / Spring Boot 3.3.x | Core framework |
| Spring Security & JWT | Authentication & security |
| Spring Data JPA | Database management |
| PostgreSQL | Primary database |
| MapStruct | DTO-Entity mapping |
| Swagger / OpenAPI | API Documentation |
| Maven | Dependency management |

### Mobile

| Technology | Usage |
|-----------|-------|
| Flutter & Dart | App development framework |
| Provider | State Management |
| Google Maps API | Maps & location services |
| Dio / Http | REST API communication |

---

## 🏗 System Architecture

The project follows a **Micro-monolith** pattern organized in clear layers:

```text
┌─────────────────────────────┐
│       Controller Layer       │  ← Handles API requests
├─────────────────────────────┤
│        Service Layer         │  ← Contains Business Logic
├─────────────────────────────┤
│       Repository Layer       │  ← Communicates with the database
├─────────────────────────────┤
│          DTOs Layer          │  ← Ensures safe & efficient data transfer
└─────────────────────────────┘
```

---

## 🌟 Key Features

### 👤 Client App
- 🗺️ Create a shipment request with map-based location selection
- 💰 Price estimate before confirmation (Calculated automatically with base fare + weight + distance)
- 🔄 Track order status in real-time:
  - ⏳ Pending
  - ✅ Accepted
  - 🚛 On the way
  - 📦 Delivered
- ⭐ Rate the driver after task completion

### 🚛 Driver App
- 🔔 Receive notifications for nearby orders
- ✅ Accept or reject orders
- 📡 Update order status in real-time for the client
- 📊 Dashboard with earnings and completed trips

---

## ⚙️ Setup & Installation

### Prerequisites

- ☕ JDK 17+
- 📱 Flutter SDK
- 🐘 PostgreSQL Server running locally or remotely
- 🗺️ Google Maps API Key

### 1. Run the Backend

```bash
# Clone the repository
git clone https://github.com/your-username/wasla.git
cd wasla/backend

# Configure your environment in src/main/resources/application.yaml
# Ensure PostgreSQL is running with matching credentials:
# URL: jdbc:postgresql://localhost:5432/postgres
# Username: postgres
# Password: postgres123
# You also need to configure your Google Maps API key and JWT Secret.

# Run the project
./mvnw spring-boot:run
```

### 2. Run the Mobile App

```bash
cd wasla/mobile

# Install dependencies
flutter pub get

# Add Google Maps API Key in AndroidManifest.xml
# <meta-data android:name="com.google.android.geo.API_KEY" android:value="YOUR_API_KEY"/>

# Run the app
flutter run
```

---

## 🔌 API Endpoints & Documentation

> **Swagger UI:** Once the backend is running, you can view the complete interactive API documentation at:  
> `http://localhost:8080/swagger-ui/index.html`

The following are key endpoints required by the Flutter mobile team (API v1).

### 1. 🔐 Authentication Controller

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/auth/register/client` | Register a new client user |
| `POST` | `/api/v1/auth/register/driver` | Register a new driver user |
| `POST` | `/api/v1/auth/login` | Returns JWT token & user Role |

---

### 2. 📦 Client Order Controller

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/orders` | Create a new order (includes initial distance and pricing calculation) |
| `GET` | `/api/v1/orders/history` | Get the client's paginated order history |
| `GET` | `/api/v1/orders/{id}` | Get order details and track its current status |

---

### 3. 🚛 Driver Controller *(Planned/In-Progress)*

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/orders/available` | Fetch nearby orders that have no assigned driver yet |
| `PUT` | `/api/v1/orders/{id}/accept` | Change order status to `ACCEPTED` and assign driver ID |
| `PUT` | `/api/v1/orders/{id}/status` | Update order status (e.g. `PICKED_UP` → `DELIVERED`) |
| `PATCH` | `/api/v1/driver/location` | Update driver's current GPS coordinates |

---

### 📋 Order Status Flow

```text
PENDING → ACCEPTED → PICKED_UP → ON_THE_WAY → DELIVERED
```

---

## 📁 Project Structure

```text
wasla/
├── backend/
│   └── src/
│       ├── config/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── dto/
│       ├── mapper/
│       ├── model/
│       └── security/
└── mobile/
    └── lib/
        ├── screens/
        ├── providers/
        ├── models/
        └── services/
```

---

## 📄 License

This project is licensed under the MIT License.

---

<div align="center">
  Made with ❤️ by the (Wasla) Team
</div>