# Implementation Plan: Entity Model Creation

## Overview

This plan implements JPA entity classes for the Wasla logistics application using Spring Boot, Hibernate, PostgreSQL, and Lombok. The implementation follows a bottom-up approach: starting with enumerations, then the base entity with auditing support, followed by domain entities (User, DriverProfile, Order, Review) with their relationships.

## Tasks

- [ ] 1. Create enumeration types
  - [x] 1.1 Create Role enumeration
    - Create `Role.java` enum in `com.example.wasla.model` package
    - Define CLIENT, DRIVER, and ADMIN values
    - _Requirements: 3.1, 3.2, 3.3, 3.4_
  
  - [x] 1.2 Create OrderStatus enumeration
    - Create `OrderStatus.java` enum in `com.example.wasla.model` package
    - Define PENDING, ACCEPTED, PICKED_UP, DELIVERED, and CANCELLED values
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6_

- [ ] 2. Create Base entity with auditing support
  - [x] 2.1 Implement BaseEntity abstract class
    - Create `BaseEntity.java` in `com.example.wasla.model` package
    - Add id field with @Id and @GeneratedValue(strategy = GenerationType.IDENTITY)
    - Add auditing fields: createdDate, lastModifiedDate, createdBy, lastModifiedBy
    - Annotate with @MappedSuperclass and @EntityListeners(AuditingEntityListener.class)
    - Use appropriate JPA auditing annotations (@CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy)
    - Add Lombok @Getter and @Setter annotations
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 8.1, 9.1, 10.1, 10.6_

- [ ] 3. Create User entity
  - [x] 3.1 Implement User entity class
    - Create `User.java` in `com.example.wasla.model` package
    - Extend BaseEntity
    - Add fields: username, email, password, phone, role
    - Annotate with @Entity and @Table(name = "users")
    - Use @Column annotations with constraints (nullable, unique, length)
    - Use @Enumerated(EnumType.STRING) for role field
    - Add Lombok annotations: @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 2.10, 8.2, 9.2, 10.2, 10.3, 10.4_

- [ ] 4. Create DriverProfile entity
  - [x] 4.1 Implement DriverProfile entity class
    - Create `DriverProfile.java` in `com.example.wasla.model` package
    - Extend BaseEntity
    - Add fields: user, truckType, licensePlate, isAvailable, currentLat, currentLng
    - Annotate with @Entity and @Table(name = "driver_profiles")
    - Use @OneToOne for user relationship with appropriate cascade and fetch type
    - Use @JoinColumn to specify foreign key column name
    - Use @Column annotations with appropriate constraints
    - Add Lombok annotations: @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 4.8, 4.9, 4.10, 4.11, 8.3, 9.3, 10.2, 10.3, 10.4_

- [ ] 5. Create Order entity
  - [x] 5.1 Implement Order entity class
    - Create `Order.java` in `com.example.wasla.model` package
    - Extend BaseEntity
    - Add fields: client, driver, status, pickupAddress, dropAddress, pickupLat, pickupLng, dropLat, dropLng, price, weight
    - Annotate with @Entity and @Table(name = "orders")
    - Use @ManyToOne for client and driver relationships with appropriate fetch type
    - Use @JoinColumn to specify foreign key column names (driver should be nullable)
    - Use @Enumerated(EnumType.STRING) for status field
    - Use @Column(precision = 10, scale = 2) for price field
    - Add Lombok annotations: @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7, 5.8, 5.9, 5.10, 5.11, 5.12, 5.13, 5.14, 5.15, 5.16, 5.17, 5.18, 5.19, 8.4, 9.4, 10.2, 10.3, 10.4, 10.5_

- [ ] 6. Create Review entity
  - [x] 6.1 Implement Review entity class
    - Create `Review.java` in `com.example.wasla.model` package
    - Extend BaseEntity
    - Add fields: order, rating, comment
    - Annotate with @Entity and @Table(name = "reviews")
    - Use @ManyToOne for order relationship with appropriate fetch type
    - Use @JoinColumn to specify foreign key column name
    - Use @Column annotations with constraints (rating validation, comment length)
    - Add Lombok annotations: @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 7.7, 7.8, 7.9, 7.10, 8.5, 9.5, 10.2, 10.3, 10.4_

- [ ] 7. Final checkpoint - Verify entity model completeness
  - Ensure all entities are in the correct package (com.example.wasla.model)
  - Verify all JPA annotations are correctly applied
  - Verify all Lombok annotations are present
  - Ensure all requirements are satisfied
  - Ask the user if questions arise

## Notes

- All entities extend BaseEntity to inherit auditing capabilities
- Enumerations are created first as they are dependencies for User and Order entities
- User entity is created before DriverProfile and Order as it's referenced by both
- PostgreSQL-compatible annotations and constraints are used throughout
- Lombok reduces boilerplate code for getters, setters, and constructors
- The implementation follows Spring Boot and JPA best practices
