# Requirements Document

## Introduction

This document specifies the requirements for implementing a comprehensive Data Transfer Object (DTO) layer for the MoveMate/Wasla logistics application. The DTO layer provides a structured interface between the Spring Boot backend and Flutter mobile application, ensuring type-safe API communication with proper validation. The DTOs are organized into request and response packages and include Bean Validation annotations to enforce data integrity at the API boundary.

## Glossary

- **DTO_Package**: The Java package structure containing all DTO classes (com.example.wasla.dto)
- **Request_DTO**: A DTO class used to receive data from API clients in HTTP request bodies
- **Response_DTO**: A DTO class used to send data to API clients in HTTP response bodies
- **Validation_Annotation**: Jakarta Bean Validation annotations that enforce constraints on DTO fields
- **Auth_System**: The authentication subsystem responsible for user registration and login
- **Order_System**: The order management subsystem handling order creation and tracking
- **Driver_System**: The driver operations subsystem managing driver actions and location
- **Review_System**: The review subsystem handling customer feedback
- **JSON_Serializer**: The Jackson library component that converts DTOs to/from JSON format

## Requirements

### Requirement 1: DTO Package Structure

**User Story:** As a backend developer, I want DTOs organized in a clear package structure, so that I can easily locate and maintain request and response objects.

#### Acceptance Criteria

1. THE DTO_Package SHALL contain a request sub-package at com.example.wasla.dto.request
2. THE DTO_Package SHALL contain a response sub-package at com.example.wasla.dto.response
3. THE Request_DTO classes SHALL reside in the request sub-package
4. THE Response_DTO classes SHALL reside in the response sub-package

### Requirement 2: Authentication Request DTOs

**User Story:** As a mobile app developer, I want to send registration and login data with validated fields, so that invalid authentication attempts are rejected before processing.

#### Acceptance Criteria

1. THE Auth_System SHALL accept a RegisterRequest DTO containing username, email, password, phone, and role fields
2. WHEN a RegisterRequest is received, THE Auth_System SHALL validate that username is not null and has length between 3 and 50 characters
3. WHEN a RegisterRequest is received, THE Auth_System SHALL validate that email is not null and matches email format pattern
4. WHEN a RegisterRequest is received, THE Auth_System SHALL validate that password is not null and has minimum length of 8 characters
5. WHEN a RegisterRequest is received, THE Auth_System SHALL validate that phone is not null and matches phone number pattern
6. WHEN a RegisterRequest is received, THE Auth_System SHALL validate that role is not null
7. THE Auth_System SHALL accept a LoginRequest DTO containing identifier and password fields
8. WHEN a LoginRequest is received, THE Auth_System SHALL validate that identifier is not blank
9. WHEN a LoginRequest is received, THE Auth_System SHALL validate that password is not blank

### Requirement 3: Authentication Response DTO

**User Story:** As a mobile app developer, I want to receive authentication results with JWT token and user information, so that I can store credentials and display user details.

#### Acceptance Criteria

1. THE Auth_System SHALL return an AuthResponse DTO containing token, userId, username, email, phone, and role fields
2. THE AuthResponse SHALL include all fields as non-null values
3. THE AuthResponse SHALL be serializable to JSON format by the JSON_Serializer

### Requirement 4: Order Creation Request DTO

**User Story:** As a client user, I want to submit order details with validated addresses and coordinates, so that invalid order data is rejected immediately.

#### Acceptance Criteria

1. THE Order_System SHALL accept a CreateOrderRequest DTO containing pickupAddress, dropAddress, pickupLat, pickupLng, dropLat, dropLng, and weight fields
2. WHEN a CreateOrderRequest is received, THE Order_System SHALL validate that pickupAddress is not blank and has maximum length of 255 characters
3. WHEN a CreateOrderRequest is received, THE Order_System SHALL validate that dropAddress is not blank and has maximum length of 255 characters
4. WHEN a CreateOrderRequest is received, THE Order_System SHALL validate that pickupLat is not null and is between -90 and 90
5. WHEN a CreateOrderRequest is received, THE Order_System SHALL validate that pickupLng is not null and is between -180 and 180
6. WHEN a CreateOrderRequest is received, THE Order_System SHALL validate that dropLat is not null and is between -90 and 90
7. WHEN a CreateOrderRequest is received, THE Order_System SHALL validate that dropLng is not null and is between -180 and 180
8. WHEN a CreateOrderRequest is received, THE Order_System SHALL validate that weight is not null and is greater than 0

### Requirement 5: Order Response DTOs

**User Story:** As a mobile app developer, I want to receive complete order information including status and timestamps, so that I can display order details and track progress.

#### Acceptance Criteria

1. THE Order_System SHALL return an OrderResponse DTO containing id, clientId, clientName, driverId, driverName, status, pickupAddress, dropAddress, pickupLat, pickupLng, dropLat, dropLng, price, weight, createdDate, and lastModifiedDate fields
2. THE OrderResponse SHALL include clientId and clientName as non-null values
3. THE OrderResponse SHALL include driverId and driverName as nullable values
4. THE OrderResponse SHALL include status as a non-null OrderStatus enum value
5. THE OrderResponse SHALL include createdDate and lastModifiedDate as non-null timestamp values
6. THE Order_System SHALL return an OrderHistoryResponse DTO containing a list of OrderResponse objects
7. THE OrderHistoryResponse SHALL be serializable to JSON format by the JSON_Serializer

### Requirement 6: Driver Operation Request DTOs

**User Story:** As a driver user, I want to submit order status updates and location data with validation, so that invalid updates are prevented.

#### Acceptance Criteria

1. THE Driver_System SHALL accept an UpdateOrderStatusRequest DTO containing status field
2. WHEN an UpdateOrderStatusRequest is received, THE Driver_System SHALL validate that status is not null
3. THE Driver_System SHALL accept an UpdateDriverLocationRequest DTO containing latitude and longitude fields
4. WHEN an UpdateDriverLocationRequest is received, THE Driver_System SHALL validate that latitude is not null and is between -90 and 90
5. WHEN an UpdateDriverLocationRequest is received, THE Driver_System SHALL validate that longitude is not null and is between -180 and 180

### Requirement 7: Available Orders Response DTO

**User Story:** As a driver user, I want to receive a list of available orders with complete details, so that I can choose which orders to accept.

#### Acceptance Criteria

1. THE Driver_System SHALL return an AvailableOrdersResponse DTO containing a list of OrderResponse objects
2. THE AvailableOrdersResponse SHALL include only orders with PENDING status
3. THE AvailableOrdersResponse SHALL be serializable to JSON format by the JSON_Serializer

### Requirement 8: Review Request DTO

**User Story:** As a client user, I want to submit review data with validated rating and comment, so that invalid reviews are rejected.

#### Acceptance Criteria

1. THE Review_System SHALL accept a CreateReviewRequest DTO containing orderId, rating, and comment fields
2. WHEN a CreateReviewRequest is received, THE Review_System SHALL validate that orderId is not null
3. WHEN a CreateReviewRequest is received, THE Review_System SHALL validate that rating is not null and is between 1 and 5
4. WHEN a CreateReviewRequest is received, THE Review_System SHALL validate that comment has maximum length of 1000 characters
5. THE CreateReviewRequest SHALL allow comment to be null or empty

### Requirement 9: Review Response DTO

**User Story:** As a mobile app developer, I want to receive review information with order and user details, so that I can display feedback in the application.

#### Acceptance Criteria

1. THE Review_System SHALL return a ReviewResponse DTO containing id, orderId, rating, comment, and createdDate fields
2. THE ReviewResponse SHALL include id, orderId, rating, and createdDate as non-null values
3. THE ReviewResponse SHALL include comment as a nullable value
4. THE ReviewResponse SHALL be serializable to JSON format by the JSON_Serializer

### Requirement 10: Validation Error Messages

**User Story:** As a mobile app developer, I want to receive clear validation error messages, so that I can display helpful feedback to users.

#### Acceptance Criteria

1. WHEN a Validation_Annotation constraint is violated, THE DTO_Package SHALL provide a descriptive error message
2. THE Validation_Annotation error messages SHALL indicate which field failed validation
3. THE Validation_Annotation error messages SHALL indicate what constraint was violated
4. THE Validation_Annotation error messages SHALL be in English language

### Requirement 11: DTO Serialization

**User Story:** As a backend developer, I want all DTOs to serialize correctly to JSON, so that the REST API can communicate with the Flutter mobile app.

#### Acceptance Criteria

1. THE Request_DTO classes SHALL be deserializable from JSON format by the JSON_Serializer
2. THE Response_DTO classes SHALL be serializable to JSON format by the JSON_Serializer
3. WHEN a DTO contains enum fields, THE JSON_Serializer SHALL serialize enums as string values
4. WHEN a DTO contains timestamp fields, THE JSON_Serializer SHALL serialize timestamps in ISO-8601 format
5. WHEN a DTO contains null fields, THE JSON_Serializer SHALL omit null fields from JSON output

### Requirement 12: DTO Immutability for Responses

**User Story:** As a backend developer, I want response DTOs to be immutable after creation, so that API responses cannot be accidentally modified.

#### Acceptance Criteria

1. THE Response_DTO classes SHALL provide constructor-based initialization
2. THE Response_DTO classes SHALL provide getter methods for all fields
3. THE Response_DTO classes SHALL not provide setter methods
4. THE Response_DTO field values SHALL not be modifiable after construction

### Requirement 13: Request DTO Mutability

**User Story:** As a backend developer, I want request DTOs to support flexible construction, so that the JSON deserializer can populate fields.

#### Acceptance Criteria

1. THE Request_DTO classes SHALL provide a no-argument constructor
2. THE Request_DTO classes SHALL provide getter methods for all fields
3. THE Request_DTO classes SHALL provide setter methods for all fields
4. THE JSON_Serializer SHALL populate Request_DTO fields using setter methods during deserialization
