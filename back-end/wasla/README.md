# 🚛 MoveMate - Smart Logistics Marketplace

> **MoveMate** is a full-stack logistics management system that connects clients who need transportation services with available truck owners.  
> Built with **Spring Boot** for the Backend and **Flutter** for mobile applications.

---

## 🚀 About the Project

### 📋 The Problem
Finding reliable transport vehicles at transparent prices is difficult, and drivers lose job opportunities due to the absence of a centralized platform.

### ✅ The Solution
A smart platform that provides:
- 📍 Live order tracking system
- 🧮 Automatic cost calculation based on distance and weight
- 📱 Dedicated interfaces for both clients and drivers

---

## 🛠 Tech Stack

### Backend

| Technology | Usage |
|-----------|-------|
| Java 21 / Spring Boot 3.x | Core framework |
| Spring Security & JWT | Authentication & security |
| Spring Data JPA | Database management |
| WebSockets (STOMP) | Real-time communication & order status |
| PostgreSQL | Primary database |
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

```
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
- 💰 Price estimate before confirmation
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
- 🐘 PostgreSQL Server
- 🗺️ Google Maps API Key

### 1. Run the Backend

```bash
# Clone the repository
git clone https://github.com/your-username/movemate.git
cd movemate/backend

# Configure the database in application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/movemate
spring.datasource.username=your_username
spring.datasource.password=your_password

# Run the project
./mvnw spring-boot:run
```

### 2. Run the Mobile App

```bash
cd movemate/mobile

# Install dependencies
flutter pub get

# Add Google Maps API Key in AndroidManifest.xml
# <meta-data android:name="com.google.android.geo.API_KEY" android:value="YOUR_API_KEY"/>

# Run the app
flutter run
```

---

## 🔌 API Endpoints

> The following endpoints are required by the Flutter mobile team.

### 1. 🔐 Authentication Controller

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Returns JWT token & user Role |

---

### 2. 📦 Client Order Controller

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/orders/create` | Create a new order (includes initial distance calculation) |
| `GET` | `/api/orders/my-history` | Get the client's order history |
| `GET` | `/api/orders/{id}` | Get order details and track its current status |

---

### 3. 🚛 Driver Controller

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/orders/available` | Fetch nearby orders that have no assigned driver yet |
| `PUT` | `/api/orders/{id}/accept` | Change order status to `ACCEPTED` and assign driver ID |
| `PUT` | `/api/orders/{id}/status` | Update order status (e.g. `PICKED_UP` → `DELIVERED`) |
| `PATCH` | `/api/driver/location` | Update driver's current GPS coordinates |

---

### 📋 Order Status Flow

```
PENDING → ACCEPTED → PICKED_UP → ON_THE_WAY → DELIVERED
```

---

## 📁 Project Structure

```
movemate/
├── backend/
│   └── src/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── dto/
│       └── model/
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
  Made with ❤️ by the MoveMate Team
</div>