# 🚛 Wasla(وصلة) - Smart Logistics Marketplace

> **Wasla** is a full-stack logistics management system that connects clients who need transportation services with available truck owners.  
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


<div align="center">
  Made with ❤️ by the Wasla Team
</div>