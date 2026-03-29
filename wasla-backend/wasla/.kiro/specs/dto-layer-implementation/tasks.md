# Implementation Plan: DTO Layer Implementation

## Overview

This plan implements a comprehensive Data Transfer Object (DTO) layer for the MoveMate/Wasla logistics application using Java, Spring Boot, Jakarta Bean Validation, and Lombok. The implementation is organized into request and response packages with declarative validation constraints and proper JSON serialization support.

## Tasks

- [x] 1. Set up DTO package structure and dependencies
  - Create package structure: `com.example.wasla.dto.request` and `com.example.wasla.dto.response`
  - Verify Lombok, Jakarta Bean Validation, and Jackson dependencies are available
  - _Requirements: 1.1, 1.2, 1.3, 1.4_

- [ ] 2. Implement authentication request DTOs
  - [x] 2.1 Create RegisterRequest DTO with validation annotations
    - Implement RegisterRequest class in request package with username, email, password, phone, and role fields
    - Add Bean Validation annotations: @NotNull, @Size, @Email, @Pattern for all fields
    - Add Lombok annotations: @Data, @NoArgsConstructor, @AllArgsConstructor
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 13.1, 13.2, 13.3, 13.4_
  
  - [ ]* 2.2 Write property test for RegisterRequest username validation
    - **Property 1: RegisterRequest Username Validation**
    - **Validates: Requirements 2.2**
  
  - [ ]* 2.3 Write property test for RegisterRequest email validation
    - **Property 2: RegisterRequest Email Validation**
    - **Validates: Requirements 2.3**
  
  - [ ]* 2.4 Write property test for RegisterRequest password validation
    - **Property 3: RegisterRequest Password Validation**
    - **Validates: Requirements 2.4**
  
  - [ ]* 2.5 Write property test for RegisterRequest phone validation
    - **Property 4: RegisterRequest Phone Validation**
    - **Validates: Requirements 2.5**
  
  - [ ]* 2.6 Write property test for RegisterRequest role validation
    - **Property 5: RegisterRequest Role Validation**
    - **Validates: Requirements 2.6**
  
  - [x] 2.7 Create LoginRequest DTO with validation annotations
    - Implement LoginRequest class in request package with identifier and password fields
    - Add Bean Validation annotations: @NotBlank for both fields
    - Add Lombok annotations: @Data, @NoArgsConstructor, @AllArgsConstructor
    - _Requirements: 2.7, 2.8, 2.9, 13.1, 13.2, 13.3, 13.4_
  
  - [ ]* 2.8 Write property test for LoginRequest field validation
    - **Property 6: LoginRequest Field Validation**
    - **Validates: Requirements 2.8, 2.9**

- [ ] 3. Implement authentication response DTO
  - [x] 3.1 Create AuthResponse DTO as immutable class
    - Implement AuthResponse class in response package with token, userId, username, email, phone, and role fields
    - Add Lombok annotations: @Getter, @AllArgsConstructor
    - Ensure all fields are non-null in constructor
    - _Requirements: 3.1, 3.2, 3.3, 12.1, 12.2, 12.3, 12.4_
  
  - [ ]* 3.2 Write property test for AuthResponse non-null fields
    - **Property 7: AuthResponse Non-Null Fields**
    - **Validates: Requirements 3.2**

- [ ] 4. Implement order request DTOs
  - [x] 4.1 Create CreateOrderRequest DTO with validation annotations
    - Implement CreateOrderRequest class in request package with pickupAddress, dropAddress, pickupLat, pickupLng, dropLat, dropLng, and weight fields
    - Add Bean Validation annotations: @NotBlank, @Size, @NotNull, @Min, @Max, @Positive for all fields
    - Add Lombok annotations: @Data, @NoArgsConstructor, @AllArgsConstructor
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 4.8, 13.1, 13.2, 13.3, 13.4_
  
  - [ ]* 4.2 Write property test for CreateOrderRequest address validation
    - **Property 8: CreateOrderRequest Address Validation**
    - **Validates: Requirements 4.2, 4.3**
  
  - [ ]* 4.3 Write property test for CreateOrderRequest coordinate validation
    - **Property 9: CreateOrderRequest Coordinate Validation**
    - **Validates: Requirements 4.4, 4.5, 4.6, 4.7**
  
  - [ ]* 4.4 Write property test for CreateOrderRequest weight validation
    - **Property 10: CreateOrderRequest Weight Validation**
    - **Validates: Requirements 4.8**

- [ ] 5. Implement order response DTOs
  - [x] 5.1 Create OrderResponse DTO as immutable class
    - Implement OrderResponse class in response package with id, clientId, clientName, driverId, driverName, status, pickupAddress, dropAddress, pickupLat, pickupLng, dropLat, dropLng, price, weight, createdDate, and lastModifiedDate fields
    - Add Lombok annotations: @Getter, @AllArgsConstructor
    - Mark driverId and driverName as nullable (allow null values)
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 12.1, 12.2, 12.3, 12.4_
  
  - [ ]* 5.2 Write property test for OrderResponse required non-null fields
    - **Property 11: OrderResponse Required Non-Null Fields**
    - **Validates: Requirements 5.2, 5.4, 5.5**
  
  - [x] 5.3 Create OrderHistoryResponse DTO as immutable class
    - Implement OrderHistoryResponse class in response package with orders field (List<OrderResponse>)
    - Add Lombok annotations: @Getter, @AllArgsConstructor
    - _Requirements: 5.6, 5.7, 12.1, 12.2, 12.3, 12.4_
  
  - [x] 5.4 Create AvailableOrdersResponse DTO as immutable class
    - Implement AvailableOrdersResponse class in response package with orders field (List<OrderResponse>)
    - Add Lombok annotations: @Getter, @AllArgsConstructor
    - _Requirements: 7.1, 7.2, 7.3, 12.1, 12.2, 12.3, 12.4_
  
  - [ ]* 5.5 Write property test for AvailableOrdersResponse status filter
    - **Property 14: AvailableOrdersResponse Status Filter**
    - **Validates: Requirements 7.2**

- [ ] 6. Implement driver operation request DTOs
  - [x] 6.1 Create UpdateOrderStatusRequest DTO with validation annotations
    - Implement UpdateOrderStatusRequest class in request package with status field
    - Add Bean Validation annotation: @NotNull for status field
    - Add Lombok annotations: @Data, @NoArgsConstructor, @AllArgsConstructor
    - _Requirements: 6.1, 6.2, 13.1, 13.2, 13.3, 13.4_
  
  - [ ]* 6.2 Write property test for UpdateOrderStatusRequest validation
    - **Property 12: UpdateOrderStatusRequest Validation**
    - **Validates: Requirements 6.2**
  
  - [x] 6.3 Create UpdateDriverLocationRequest DTO with validation annotations
    - Implement UpdateDriverLocationRequest class in request package with latitude and longitude fields
    - Add Bean Validation annotations: @NotNull, @Min, @Max for both fields
    - Add Lombok annotations: @Data, @NoArgsConstructor, @AllArgsConstructor
    - _Requirements: 6.3, 6.4, 6.5, 13.1, 13.2, 13.3, 13.4_
  
  - [ ]* 6.4 Write property test for UpdateDriverLocationRequest coordinate validation
    - **Property 13: UpdateDriverLocationRequest Coordinate Validation**
    - **Validates: Requirements 6.4, 6.5**

- [ ] 7. Implement review DTOs
  - [x] 7.1 Create CreateReviewRequest DTO with validation annotations
    - Implement CreateReviewRequest class in request package with orderId, rating, and comment fields
    - Add Bean Validation annotations: @NotNull for orderId and rating, @Min/@Max for rating, @Size for comment
    - Add Lombok annotations: @Data, @NoArgsConstructor, @AllArgsConstructor
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5, 13.1, 13.2, 13.3, 13.4_
  
  - [ ]* 7.2 Write property test for CreateReviewRequest orderId validation
    - **Property 15: CreateReviewRequest OrderId Validation**
    - **Validates: Requirements 8.2**
  
  - [ ]* 7.3 Write property test for CreateReviewRequest rating validation
    - **Property 16: CreateReviewRequest Rating Validation**
    - **Validates: Requirements 8.3**
  
  - [ ]* 7.4 Write property test for CreateReviewRequest comment validation
    - **Property 17: CreateReviewRequest Comment Validation**
    - **Validates: Requirements 8.4, 8.5**
  
  - [x] 7.5 Create ReviewResponse DTO as immutable class
    - Implement ReviewResponse class in response package with id, orderId, rating, comment, and createdDate fields
    - Add Lombok annotations: @Getter, @AllArgsConstructor
    - Mark comment as nullable (allow null values)
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 12.1, 12.2, 12.3, 12.4_
  
  - [ ]* 7.6 Write property test for ReviewResponse required non-null fields
    - **Property 18: ReviewResponse Required Non-Null Fields**
    - **Validates: Requirements 9.2**

- [x] 8. Checkpoint - Verify all DTOs compile and basic structure is correct
  - Ensure all DTOs compile without errors, ask the user if questions arise.

- [ ] 9. Configure JSON serialization and validation error handling
  - [x] 9.1 Configure Jackson JSON serialization settings
    - Add or verify application.properties configuration for Jackson (NON_NULL inclusion, ISO-8601 dates, ignore unknown properties)
    - _Requirements: 11.3, 11.4, 11.5_
  
  - [x] 9.2 Create ValidationExceptionHandler for consistent error responses
    - Implement @ControllerAdvice class to handle MethodArgumentNotValidException
    - Create ValidationErrorResponse and FieldError DTOs for error formatting
    - _Requirements: 10.1, 10.2, 10.3, 10.4_
  
  - [ ]* 9.3 Write property test for validation error completeness
    - **Property 19: Validation Error Completeness**
    - **Validates: Requirements 10.1, 10.2, 10.3**

- [ ] 10. Implement JSON serialization tests
  - [ ]* 10.1 Write property test for request DTO JSON round-trip
    - **Property 20: Request DTO JSON Round-Trip**
    - **Validates: Requirements 11.1, 13.4**
  
  - [ ]* 10.2 Write property test for response DTO JSON round-trip
    - **Property 21: Response DTO JSON Round-Trip**
    - **Validates: Requirements 3.3, 5.7, 7.3, 9.4, 11.2**
  
  - [ ]* 10.3 Write property test for enum serialization format
    - **Property 22: Enum Serialization Format**
    - **Validates: Requirements 11.3**
  
  - [ ]* 10.4 Write property test for timestamp serialization format
    - **Property 23: Timestamp Serialization Format**
    - **Validates: Requirements 11.4**
  
  - [ ]* 10.5 Write property test for null field omission
    - **Property 24: Null Field Omission**
    - **Validates: Requirements 11.5**

- [ ] 11. Write unit tests for all DTOs
  - [ ]* 11.1 Write unit tests for RegisterRequest
    - Test valid construction, invalid field values, boundary values, validation error messages
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6_
  
  - [ ]* 11.2 Write unit tests for LoginRequest
    - Test valid construction, blank field validation, error messages
    - _Requirements: 2.7, 2.8, 2.9_
  
  - [ ]* 11.3 Write unit tests for AuthResponse
    - Test construction, JSON serialization, field access
    - _Requirements: 3.1, 3.2, 3.3_
  
  - [ ]* 11.4 Write unit tests for CreateOrderRequest
    - Test valid construction, address validation, coordinate validation, weight validation, error messages
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 4.8_
  
  - [ ]* 11.5 Write unit tests for OrderResponse, OrderHistoryResponse, and AvailableOrdersResponse
    - Test construction, JSON serialization, nullable fields, timestamp formatting
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7, 7.1, 7.2, 7.3_
  
  - [ ]* 11.6 Write unit tests for UpdateOrderStatusRequest and UpdateDriverLocationRequest
    - Test valid construction, validation constraints, error messages
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_
  
  - [ ]* 11.7 Write unit tests for CreateReviewRequest and ReviewResponse
    - Test valid construction, rating validation, comment validation, nullable comment, error messages
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5, 9.1, 9.2, 9.3, 9.4_

- [ ] 12. Final checkpoint - Ensure all tests pass and DTOs are ready for integration
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Property tests validate universal correctness properties across all inputs
- Unit tests validate specific examples, edge cases, and error messages
- All DTOs use Lombok to reduce boilerplate code
- Request DTOs are mutable (support JSON deserialization), response DTOs are immutable
- Bean Validation annotations provide declarative validation at the API boundary
- Jackson handles JSON serialization with configured settings for null omission and timestamp formatting
