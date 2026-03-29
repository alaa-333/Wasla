# Requirements Document

## Introduction

This document specifies the requirements for creating JPA entity classes for a logistics application (Wasla) that manages users, drivers, orders, and reviews. The system uses Spring Boot with Hibernate ORM and PostgreSQL database, supporting a transportation service where clients can request deliveries and drivers can fulfill them.

## Glossary

- **Entity_Model**: A Java class annotated with JPA annotations that maps to a database table
- **Base_Entity**: An abstract entity class containing common fields inherited by all entities
- **User_Entity**: Entity representing system users (clients, drivers, and administrators)
- **DriverProfile_Entity**: Entity containing driver-specific information and availability status
- **Order_Entity**: Entity representing a delivery order from pickup to drop-off location
- **Review_Entity**: Entity representing customer feedback for completed orders
- **Auditing_Fields**: Timestamp fields automatically managed by Spring Data JPA (createdDate, lastModifiedDate, createdBy, lastModifiedBy)
- **Role_Enum**: Enumeration defining user types (CLIENT, DRIVER, ADMIN)
- **OrderStatus_Enum**: Enumeration defining order lifecycle states (PENDING, ACCEPTED, PICKED_UP, DELIVERED, CANCELLED)
- **Hibernate**: JPA implementation used for object-relational mapping
- **PostgreSQL**: Relational database management system used for data persistence

## Requirements

### Requirement 1: Base Entity with Auditing Support

**User Story:** As a developer, I want a base entity class with common fields, so that all entities inherit standard auditing capabilities without duplication.

#### Acceptance Criteria

1. THE Base_Entity SHALL be an abstract class
2. THE Base_Entity SHALL include an id field of type Long as the primary key
3. THE Base_Entity SHALL include createdDate field of type LocalDateTime
4. THE Base_Entity SHALL include lastModifiedDate field of type LocalDateTime
5. THE Base_Entity SHALL include createdBy field of type String
6. THE Base_Entity SHALL include lastModifiedBy field of type String
7. THE Base_Entity SHALL be annotated with @MappedSuperclass
8. THE Base_Entity SHALL be annotated with @EntityListeners(AuditingEntityListener.class)
9. THE Base_Entity SHALL use appropriate JPA annotations for auditing fields (@CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy)
10. FOR ALL entities extending Base_Entity, the auditing fields SHALL be automatically populated by Spring Data JPA

### Requirement 2: User Entity Creation

**User Story:** As a developer, I want a User entity to store user account information, so that the system can authenticate and manage different user types.

#### Acceptance Criteria

1. THE User_Entity SHALL extend Base_Entity
2. THE User_Entity SHALL include a username field of type String
3. THE User_Entity SHALL include an email field of type String
4. THE User_Entity SHALL include a password field of type String
5. THE User_Entity SHALL include a phone field of type String
6. THE User_Entity SHALL include a role field of type Role_Enum
7. THE User_Entity SHALL be annotated with @Entity
8. THE User_Entity SHALL be annotated with @Table specifying the table name as "users"
9. THE User_Entity SHALL use @Column annotations with appropriate constraints (nullable, unique, length)
10. THE User_Entity SHALL use @Enumerated(EnumType.STRING) for the role field

### Requirement 3: Role Enumeration

**User Story:** As a developer, I want a Role enumeration, so that user types are type-safe and constrained to valid values.

#### Acceptance Criteria

1. THE Role_Enum SHALL be a Java enum
2. THE Role_Enum SHALL include CLIENT as a value
3. THE Role_Enum SHALL include DRIVER as a value
4. THE Role_Enum SHALL include ADMIN as a value

### Requirement 4: DriverProfile Entity Creation

**User Story:** As a developer, I want a DriverProfile entity to store driver-specific information, so that the system can track driver availability and vehicle details.

#### Acceptance Criteria

1. THE DriverProfile_Entity SHALL extend Base_Entity
2. THE DriverProfile_Entity SHALL include a user field establishing a one-to-one relationship with User_Entity
3. THE DriverProfile_Entity SHALL include a truckType field of type String
4. THE DriverProfile_Entity SHALL include a licensePlate field of type String
5. THE DriverProfile_Entity SHALL include an isAvailable field of type Boolean
6. THE DriverProfile_Entity SHALL include a currentLat field of type Double
7. THE DriverProfile_Entity SHALL include a currentLng field of type Double
8. THE DriverProfile_Entity SHALL be annotated with @Entity
9. THE DriverProfile_Entity SHALL be annotated with @Table specifying the table name as "driver_profiles"
10. THE DriverProfile_Entity SHALL use @OneToOne annotation with appropriate cascade and fetch type for the user relationship
11. THE DriverProfile_Entity SHALL use @JoinColumn to specify the foreign key column name

### Requirement 5: Order Entity Creation

**User Story:** As a developer, I want an Order entity to store delivery order information, so that the system can track orders from creation to completion.

#### Acceptance Criteria

1. THE Order_Entity SHALL extend Base_Entity
2. THE Order_Entity SHALL include a client field establishing a many-to-one relationship with User_Entity
3. THE Order_Entity SHALL include a driver field establishing a many-to-one relationship with User_Entity
4. THE Order_Entity SHALL include a status field of type OrderStatus_Enum
5. THE Order_Entity SHALL include a pickupAddress field of type String
6. THE Order_Entity SHALL include a dropAddress field of type String
7. THE Order_Entity SHALL include a pickupLat field of type Double
8. THE Order_Entity SHALL include a pickupLng field of type Double
9. THE Order_Entity SHALL include a dropLat field of type Double
10. THE Order_Entity SHALL include a dropLng field of type Double
11. THE Order_Entity SHALL include a price field of type BigDecimal
12. THE Order_Entity SHALL include a weight field of type Double
13. THE Order_Entity SHALL be annotated with @Entity
14. THE Order_Entity SHALL be annotated with @Table specifying the table name as "orders"
15. THE Order_Entity SHALL use @ManyToOne annotation with appropriate fetch type for client and driver relationships
16. THE Order_Entity SHALL use @JoinColumn to specify foreign key column names for client and driver
17. THE Order_Entity SHALL use @Enumerated(EnumType.STRING) for the status field
18. WHEN an order is created, THE driver field SHALL be nullable
19. THE Order_Entity SHALL use @Column(precision, scale) for the price field to ensure proper decimal handling

### Requirement 6: OrderStatus Enumeration

**User Story:** As a developer, I want an OrderStatus enumeration, so that order states are type-safe and follow a defined lifecycle.

#### Acceptance Criteria

1. THE OrderStatus_Enum SHALL be a Java enum
2. THE OrderStatus_Enum SHALL include PENDING as a value
3. THE OrderStatus_Enum SHALL include ACCEPTED as a value
4. THE OrderStatus_Enum SHALL include PICKED_UP as a value
5. THE OrderStatus_Enum SHALL include DELIVERED as a value
6. THE OrderStatus_Enum SHALL include CANCELLED as a value

### Requirement 7: Review Entity Creation

**User Story:** As a developer, I want a Review entity to store customer feedback, so that the system can track service quality and driver ratings.

#### Acceptance Criteria

1. THE Review_Entity SHALL extend Base_Entity
2. THE Review_Entity SHALL include an order field establishing a many-to-one relationship with Order_Entity
3. THE Review_Entity SHALL include a rating field of type Integer
4. THE Review_Entity SHALL include a comment field of type String
5. THE Review_Entity SHALL be annotated with @Entity
6. THE Review_Entity SHALL be annotated with @Table specifying the table name as "reviews"
7. THE Review_Entity SHALL use @ManyToOne annotation with appropriate fetch type for the order relationship
8. THE Review_Entity SHALL use @JoinColumn to specify the foreign key column name for order
9. THE Review_Entity SHALL use @Column annotation with constraints to ensure rating is between 1 and 5
10. THE Review_Entity SHALL use @Column annotation with appropriate length for the comment field

### Requirement 8: Entity Package Organization

**User Story:** As a developer, I want all entities organized in the model package, so that the codebase follows standard Spring Boot project structure.

#### Acceptance Criteria

1. THE Base_Entity SHALL be located in package com.example.wasla.model
2. THE User_Entity SHALL be located in package com.example.wasla.model
3. THE DriverProfile_Entity SHALL be located in package com.example.wasla.model
4. THE Order_Entity SHALL be located in package com.example.wasla.model
5. THE Review_Entity SHALL be located in package com.example.wasla.model
6. THE Role_Enum SHALL be located in package com.example.wasla.model
7. THE OrderStatus_Enum SHALL be located in package com.example.wasla.model

### Requirement 9: Lombok Integration

**User Story:** As a developer, I want entities to use Lombok annotations, so that boilerplate code for getters, setters, and constructors is automatically generated.

#### Acceptance Criteria

1. WHERE Lombok is available in the project, THE Base_Entity SHALL use @Getter and @Setter annotations
2. WHERE Lombok is available in the project, THE User_Entity SHALL use @Getter, @Setter, @NoArgsConstructor, and @AllArgsConstructor annotations
3. WHERE Lombok is available in the project, THE DriverProfile_Entity SHALL use @Getter, @Setter, @NoArgsConstructor, and @AllArgsConstructor annotations
4. WHERE Lombok is available in the project, THE Order_Entity SHALL use @Getter, @Setter, @NoArgsConstructor, and @AllArgsConstructor annotations
5. WHERE Lombok is available in the project, THE Review_Entity SHALL use @Getter, @Setter, @NoArgsConstructor, and @AllArgsConstructor annotations

### Requirement 10: Database Schema Compatibility

**User Story:** As a developer, I want entities compatible with PostgreSQL, so that Hibernate can generate correct DDL statements and manage the database schema.

#### Acceptance Criteria

1. THE Base_Entity id field SHALL use @GeneratedValue with strategy GenerationType.IDENTITY
2. FOR ALL entities, column names SHALL follow PostgreSQL naming conventions (snake_case)
3. FOR ALL entities with String fields, appropriate length constraints SHALL be specified
4. FOR ALL entities, the @Table annotation SHALL specify explicit table names
5. THE Order_Entity price field SHALL use precision and scale appropriate for currency values in PostgreSQL
6. FOR ALL timestamp fields in Base_Entity, the column type SHALL be compatible with PostgreSQL TIMESTAMP
