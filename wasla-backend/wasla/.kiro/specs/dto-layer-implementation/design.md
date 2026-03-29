# Design Document: DTO Layer Implementation

## Overview

This design document specifies the implementation of a comprehensive Data Transfer Object (DTO) layer for the MoveMate/Wasla logistics application. The DTO layer serves as the contract between the Spring Boot REST API and the Flutter mobile client, providing type-safe data structures with built-in validation.

The design leverages Jakarta Bean Validation (JSR-380) for declarative constraint validation and Lombok annotations to reduce boilerplate code. DTOs are organized into request and response packages, with request DTOs being mutable to support JSON deserialization and response DTOs being immutable to prevent accidental modification.

Key design goals:
- Clear separation between request and response DTOs
- Declarative validation at the API boundary
- Minimal boilerplate through Lombok
- Proper JSON serialization/deserialization
- Type safety and compile-time checking

## Architecture

### Package Structure

```
com.example.wasla.dto
├── request
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── CreateOrderRequest.java
│   ├── UpdateOrderStatusRequest.java
│   ├── UpdateDriverLocationRequest.java
│   └── CreateReviewRequest.java
└── response
    ├── AuthResponse.java
    ├── OrderResponse.java
    ├── OrderHistoryResponse.java
    ├── AvailableOrdersResponse.java
    └── ReviewResponse.java
```

### Architectural Layers

```
┌─────────────────────────────────────┐
│     Flutter Mobile Client           │
│         (JSON HTTP)                 │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│    Spring Boot Controllers          │
│  (@RequestBody / @ResponseBody)     │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      Jackson JSON Mapper            │
│   (Serialization/Deserialization)   │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│         DTO Layer                   │
│  ┌─────────────┬─────────────┐     │
│  │  Request    │  Response   │     │
│  │   DTOs      │    DTOs     │     │
│  │  (Mutable)  │ (Immutable) │     │
│  └─────────────┴─────────────┘     │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│    Bean Validation Framework        │
│      (Constraint Validation)        │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│       Service Layer                 │
│   (Business Logic Processing)       │
└─────────────────────────────────────┘
```

### Design Patterns

1. **Data Transfer Object Pattern**: Encapsulates data for transfer between layers
2. **Builder Pattern**: Provided by Lombok for flexible object construction
3. **Immutable Object Pattern**: Applied to response DTOs for thread safety
4. **Validation Pattern**: Declarative constraints using Bean Validation annotations

### Technology Stack

- **Java 17+**: Language version
- **Jakarta Bean Validation 3.0**: Validation framework
- **Hibernate Validator**: Bean Validation implementation
- **Lombok**: Boilerplate reduction
- **Jackson**: JSON serialization/deserialization
- **Spring Boot**: Framework integration

## Components and Interfaces

### Request DTOs

Request DTOs are mutable objects designed to receive data from API clients. They use Lombok's `@Data` annotation to generate getters, setters, equals, hashCode, and toString methods, along with `@NoArgsConstructor` and `@AllArgsConstructor` for flexible construction.

#### RegisterRequest

**Purpose**: Captures user registration data with validation

**Package**: `com.example.wasla.dto.request`

**Fields**:
- `username` (String): User's chosen username
  - Constraints: `@NotNull`, `@Size(min=3, max=50)`
  - Message: "Username must be between 3 and 50 characters"
  
- `email` (String): User's email address
  - Constraints: `@NotNull`, `@Email`
  - Message: "Email must be a valid email address"
  
- `password` (String): User's password
  - Constraints: `@NotNull`, `@Size(min=8)`
  - Message: "Password must be at least 8 characters"
  
- `phone` (String): User's phone number
  - Constraints: `@NotNull`, `@Pattern(regexp="^\\+?[1-9]\\d{1,14}$")`
  - Message: "Phone must be a valid phone number"
  
- `role` (String): User's role (CLIENT or DRIVER)
  - Constraints: `@NotNull`
  - Message: "Role is required"

**Lombok Annotations**: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`

#### LoginRequest

**Purpose**: Captures login credentials

**Package**: `com.example.wasla.dto.request`

**Fields**:
- `identifier` (String): Username, email, or phone
  - Constraints: `@NotBlank`
  - Message: "Identifier is required"
  
- `password` (String): User's password
  - Constraints: `@NotBlank`
  - Message: "Password is required"

**Lombok Annotations**: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`

#### CreateOrderRequest

**Purpose**: Captures order creation data with location validation

**Package**: `com.example.wasla.dto.request`

**Fields**:
- `pickupAddress` (String): Pickup location address
  - Constraints: `@NotBlank`, `@Size(max=255)`
  - Message: "Pickup address is required and must not exceed 255 characters"
  
- `dropAddress` (String): Delivery location address
  - Constraints: `@NotBlank`, `@Size(max=255)`
  - Message: "Drop address is required and must not exceed 255 characters"
  
- `pickupLat` (Double): Pickup latitude
  - Constraints: `@NotNull`, `@Min(-90)`, `@Max(90)`
  - Message: "Pickup latitude must be between -90 and 90"
  
- `pickupLng` (Double): Pickup longitude
  - Constraints: `@NotNull`, `@Min(-180)`, `@Max(180)`
  - Message: "Pickup longitude must be between -180 and 180"
  
- `dropLat` (Double): Drop-off latitude
  - Constraints: `@NotNull`, `@Min(-90)`, `@Max(90)`
  - Message: "Drop latitude must be between -90 and 90"
  
- `dropLng` (Double): Drop-off longitude
  - Constraints: `@NotNull`, `@Min(-180)`, `@Max(180)`
  - Message: "Drop longitude must be between -180 and 180"
  
- `weight` (Double): Package weight in kg
  - Constraints: `@NotNull`, `@Positive`
  - Message: "Weight must be greater than 0"

**Lombok Annotations**: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`

#### UpdateOrderStatusRequest

**Purpose**: Captures order status update from driver

**Package**: `com.example.wasla.dto.request`

**Fields**:
- `status` (String): New order status
  - Constraints: `@NotNull`
  - Message: "Status is required"

**Lombok Annotations**: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`

#### UpdateDriverLocationRequest

**Purpose**: Captures driver's current location

**Package**: `com.example.wasla.dto.request`

**Fields**:
- `latitude` (Double): Driver's current latitude
  - Constraints: `@NotNull`, `@Min(-90)`, `@Max(90)`
  - Message: "Latitude must be between -90 and 90"
  
- `longitude` (Double): Driver's current longitude
  - Constraints: `@NotNull`, `@Min(-180)`, `@Max(180)`
  - Message: "Longitude must be between -180 and 180"

**Lombok Annotations**: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`

#### CreateReviewRequest

**Purpose**: Captures review submission data

**Package**: `com.example.wasla.dto.request`

**Fields**:
- `orderId` (Long): ID of the order being reviewed
  - Constraints: `@NotNull`
  - Message: "Order ID is required"
  
- `rating` (Integer): Rating from 1 to 5
  - Constraints: `@NotNull`, `@Min(1)`, `@Max(5)`
  - Message: "Rating must be between 1 and 5"
  
- `comment` (String): Optional review comment
  - Constraints: `@Size(max=1000)`
  - Message: "Comment must not exceed 1000 characters"

**Lombok Annotations**: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`

### Response DTOs

Response DTOs are immutable objects designed to send data to API clients. They use Lombok's `@Getter` and `@AllArgsConstructor` annotations to provide read-only access with constructor-based initialization.

#### AuthResponse

**Purpose**: Returns authentication result with JWT token and user info

**Package**: `com.example.wasla.dto.response`

**Fields**:
- `token` (String): JWT authentication token
- `userId` (Long): User's unique identifier
- `username` (String): User's username
- `email` (String): User's email
- `phone` (String): User's phone number
- `role` (String): User's role

**Lombok Annotations**: `@Getter`, `@AllArgsConstructor`

**JSON Configuration**: All fields are non-null and will be included in JSON output

#### OrderResponse

**Purpose**: Returns complete order information

**Package**: `com.example.wasla.dto.response`

**Fields**:
- `id` (Long): Order unique identifier
- `clientId` (Long): Client user ID
- `clientName` (String): Client username
- `driverId` (Long): Driver user ID (nullable)
- `driverName` (String): Driver username (nullable)
- `status` (String): Order status enum value
- `pickupAddress` (String): Pickup location
- `dropAddress` (String): Delivery location
- `pickupLat` (Double): Pickup latitude
- `pickupLng` (Double): Pickup longitude
- `dropLat` (Double): Drop-off latitude
- `dropLng` (Double): Drop-off longitude
- `price` (Double): Order price
- `weight` (Double): Package weight
- `createdDate` (LocalDateTime): Order creation timestamp
- `lastModifiedDate` (LocalDateTime): Last update timestamp

**Lombok Annotations**: `@Getter`, `@AllArgsConstructor`

**JSON Configuration**: 
- Nullable fields (driverId, driverName) will be omitted if null
- Timestamps serialized in ISO-8601 format
- Status serialized as string

#### OrderHistoryResponse

**Purpose**: Returns list of orders for a user

**Package**: `com.example.wasla.dto.response`

**Fields**:
- `orders` (List<OrderResponse>): List of order objects

**Lombok Annotations**: `@Getter`, `@AllArgsConstructor`

#### AvailableOrdersResponse

**Purpose**: Returns list of available orders for drivers

**Package**: `com.example.wasla.dto.response`

**Fields**:
- `orders` (List<OrderResponse>): List of pending order objects

**Lombok Annotations**: `@Getter`, `@AllArgsConstructor`

#### ReviewResponse

**Purpose**: Returns review information

**Package**: `com.example.wasla.dto.response`

**Fields**:
- `id` (Long): Review unique identifier
- `orderId` (Long): Associated order ID
- `rating` (Integer): Rating value (1-5)
- `comment` (String): Review comment (nullable)
- `createdDate` (LocalDateTime): Review creation timestamp

**Lombok Annotations**: `@Getter`, `@AllArgsConstructor`

**JSON Configuration**: 
- Nullable comment field will be omitted if null
- Timestamp serialized in ISO-8601 format

## Data Models

### Validation Constraint Summary

| Constraint | Purpose | Example Usage |
|------------|---------|---------------|
| `@NotNull` | Field must not be null | Required fields |
| `@NotBlank` | String must not be null, empty, or whitespace | Login credentials |
| `@Size(min, max)` | String/Collection size bounds | Username length |
| `@Min(value)` | Numeric minimum value | Latitude >= -90 |
| `@Max(value)` | Numeric maximum value | Latitude <= 90 |
| `@Positive` | Number must be > 0 | Weight validation |
| `@Email` | Valid email format | Email addresses |
| `@Pattern(regexp)` | Matches regex pattern | Phone numbers |

### JSON Serialization Configuration

The application uses Jackson for JSON processing with the following configuration:

**Global Configuration** (application.properties):
```properties
spring.jackson.default-property-inclusion=NON_NULL
spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false
spring.jackson.deserialization.FAIL_ON_UNKNOWN_PROPERTIES=false
```

**Behavior**:
- Null fields are omitted from JSON output
- Dates serialized as ISO-8601 strings (e.g., "2024-01-15T10:30:00")
- Unknown properties in input JSON are ignored (forward compatibility)
- Enum values serialized as strings

### DTO Lifecycle

```
Request Flow:
JSON → Jackson Deserializer → Request DTO → Bean Validator → Service Layer

Response Flow:
Service Layer → Response DTO → Jackson Serializer → JSON
```

### Validation Error Response Format

When validation fails, Spring Boot returns a standardized error response:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "errors": [
    {
      "field": "email",
      "rejectedValue": "invalid-email",
      "message": "Email must be a valid email address"
    }
  ],
  "path": "/api/auth/register"
}
```


## Correctness Properties

A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.

### Property Reflection

After analyzing all acceptance criteria, I identified several areas where properties can be consolidated:

1. **Coordinate validation properties**: All latitude/longitude validations follow the same pattern (lat: -90 to 90, lng: -180 to 180). These can be combined into comprehensive coordinate validation properties rather than separate properties for each DTO field.

2. **Non-null field properties**: Multiple requirements specify that certain response DTO fields must be non-null. These can be combined into single properties per DTO rather than per field.

3. **JSON serialization properties**: Multiple DTOs have serialization requirements. These can be consolidated into general serialization properties that apply to all DTOs.

4. **Validation error properties**: Requirements 10.1, 10.2, and 10.3 all relate to validation error messages and can be combined into a single comprehensive property.

5. **Address validation properties**: pickupAddress and dropAddress have identical validation rules and can be tested with a single property.

### Property 1: RegisterRequest Username Validation

For any RegisterRequest instance, if the username is null or has length outside the range [3, 50], then Bean Validation should reject it with a constraint violation.

**Validates: Requirements 2.2**

### Property 2: RegisterRequest Email Validation

For any RegisterRequest instance, if the email is null or does not match valid email format, then Bean Validation should reject it with a constraint violation.

**Validates: Requirements 2.3**

### Property 3: RegisterRequest Password Validation

For any RegisterRequest instance, if the password is null or has length less than 8 characters, then Bean Validation should reject it with a constraint violation.

**Validates: Requirements 2.4**

### Property 4: RegisterRequest Phone Validation

For any RegisterRequest instance, if the phone is null or does not match the phone number pattern, then Bean Validation should reject it with a constraint violation.

**Validates: Requirements 2.5**

### Property 5: RegisterRequest Role Validation

For any RegisterRequest instance, if the role is null, then Bean Validation should reject it with a constraint violation.

**Validates: Requirements 2.6**

### Property 6: LoginRequest Field Validation

For any LoginRequest instance, if either identifier or password is blank (null, empty, or whitespace-only), then Bean Validation should reject it with a constraint violation.

**Validates: Requirements 2.8, 2.9**

### Property 7: AuthResponse Non-Null Fields

For any AuthResponse instance, all fields (token, userId, username, email, phone, role) should be non-null after construction.

**Validates: Requirements 3.2**

### Property 8: CreateOrderRequest Address Validation

For any CreateOrderRequest instance, if either pickupAddress or dropAddress is blank or exceeds 255 characters, then Bean Validation should reject it with a constraint violation.

**Validates: Requirements 4.2, 4.3**

### Property 9: CreateOrderRequest Coordinate Validation

For any CreateOrderRequest instance, if any coordinate field (pickupLat, pickupLng, dropLat, dropLng) is null or outside its valid range (latitude: [-90, 90], longitude: [-180, 180]), then Bean Validation should reject it with a constraint violation.

**Validates: Requirements 4.4, 4.5, 4.6, 4.7**

### Property 10: CreateOrderRequest Weight Validation

For any CreateOrderRequest instance, if weight is null or not positive (≤ 0), then Bean Validation should reject it with a constraint violation.

**Validates: Requirements 4.8**

### Property 11: OrderResponse Required Non-Null Fields

For any OrderResponse instance, the fields clientId, clientName, status, createdDate, and lastModifiedDate should be non-null after construction.

**Validates: Requirements 5.2, 5.4, 5.5**

### Property 12: UpdateOrderStatusRequest Validation

For any UpdateOrderStatusRequest instance, if status is null, then Bean Validation should reject it with a constraint violation.

**Validates: Requirements 6.2**

### Property 13: UpdateDriverLocationRequest Coordinate Validation

For any UpdateDriverLocationRequest instance, if latitude is null or outside [-90, 90], or if longitude is null or outside [-180, 180], then Bean Validation should reject it with a constraint violation.

**Validates: Requirements 6.4, 6.5**

### Property 14: AvailableOrdersResponse Status Filter

For any AvailableOrdersResponse instance, all orders in the list should have status equal to "PENDING".

**Validates: Requirements 7.2**

### Property 15: CreateReviewRequest OrderId Validation

For any CreateReviewRequest instance, if orderId is null, then Bean Validation should reject it with a constraint violation.

**Validates: Requirements 8.2**

### Property 16: CreateReviewRequest Rating Validation

For any CreateReviewRequest instance, if rating is null or outside the range [1, 5], then Bean Validation should reject it with a constraint violation.

**Validates: Requirements 8.3**

### Property 17: CreateReviewRequest Comment Validation

For any CreateReviewRequest instance, if comment exceeds 1000 characters, then Bean Validation should reject it with a constraint violation. Null and empty comments should be accepted.

**Validates: Requirements 8.4, 8.5**

### Property 18: ReviewResponse Required Non-Null Fields

For any ReviewResponse instance, the fields id, orderId, rating, and createdDate should be non-null after construction.

**Validates: Requirements 9.2**

### Property 19: Validation Error Completeness

For any DTO instance that fails Bean Validation, the resulting constraint violations should include a descriptive error message, the field name that failed validation, and information about the constraint that was violated.

**Validates: Requirements 10.1, 10.2, 10.3**

### Property 20: Request DTO JSON Round-Trip

For any Request DTO instance (RegisterRequest, LoginRequest, CreateOrderRequest, UpdateOrderStatusRequest, UpdateDriverLocationRequest, CreateReviewRequest), serializing to JSON and then deserializing back should produce an equivalent object with all field values preserved.

**Validates: Requirements 11.1, 13.4**

### Property 21: Response DTO JSON Round-Trip

For any Response DTO instance (AuthResponse, OrderResponse, OrderHistoryResponse, AvailableOrdersResponse, ReviewResponse), serializing to JSON and then deserializing back should produce an equivalent object with all field values preserved.

**Validates: Requirements 3.3, 5.7, 7.3, 9.4, 11.2**

### Property 22: Enum Serialization Format

For any DTO instance containing enum fields (e.g., OrderResponse with status), serializing to JSON should produce string values for enum fields, not numeric values.

**Validates: Requirements 11.3**

### Property 23: Timestamp Serialization Format

For any DTO instance containing LocalDateTime fields (e.g., OrderResponse with createdDate), serializing to JSON should produce ISO-8601 formatted timestamp strings (e.g., "2024-01-15T10:30:00").

**Validates: Requirements 11.4**

### Property 24: Null Field Omission

For any Response DTO instance with nullable fields set to null (e.g., OrderResponse with null driverId), serializing to JSON should omit those null fields from the output.

**Validates: Requirements 11.5**

## Error Handling

### Validation Error Handling

The DTO layer relies on Spring Boot's automatic validation error handling through `@Valid` annotation on controller method parameters. When validation fails:

1. **Exception Type**: `MethodArgumentNotValidException` is thrown
2. **HTTP Status**: 400 Bad Request is returned
3. **Error Response**: Spring Boot's default error handler formats validation errors

### Custom Error Response

To provide consistent error responses, implement a `@ControllerAdvice` class:

```java
@ControllerAdvice
public class ValidationExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidationException(
            MethodArgumentNotValidException ex) {
        
        List<FieldError> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new FieldError(
                error.getField(),
                error.getRejectedValue(),
                error.getDefaultMessage()
            ))
            .collect(Collectors.toList());
        
        return new ValidationErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            fieldErrors
        );
    }
}
```

### Validation Error Response DTO

```java
@Getter
@AllArgsConstructor
public class ValidationErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private List<FieldError> errors;
}

@Getter
@AllArgsConstructor
public class FieldError {
    private String field;
    private Object rejectedValue;
    private String message;
}
```

### Error Scenarios

| Scenario | HTTP Status | Error Type |
|----------|-------------|------------|
| Validation constraint violated | 400 | Bad Request |
| Malformed JSON | 400 | Bad Request |
| Missing required field | 400 | Bad Request |
| Type mismatch (e.g., string for number) | 400 | Bad Request |
| Unknown properties in JSON | Ignored | N/A (forward compatibility) |

## Testing Strategy

### Dual Testing Approach

The DTO layer requires both unit tests and property-based tests for comprehensive coverage:

**Unit Tests**: Focus on specific examples, edge cases, and integration with validation framework
- Specific valid/invalid examples for each DTO
- Edge cases (boundary values, special characters)
- JSON serialization/deserialization examples
- Error message verification

**Property-Based Tests**: Verify universal properties across all possible inputs
- Validation rules hold for all generated inputs
- Round-trip serialization preserves data
- Coordinate ranges are enforced
- Non-null constraints are maintained

### Property-Based Testing Configuration

**Framework**: Use **fast-check** for TypeScript/JavaScript or **jqwik** for Java

For Java with jqwik, add dependency:
```xml
<dependency>
    <groupId>net.jqwik</groupId>
    <artifactId>jqwik</artifactId>
    <version>1.7.4</version>
    <scope>test</scope>
</dependency>
```

**Configuration**:
- Minimum 100 iterations per property test
- Each test must reference its design document property
- Tag format: `@Tag("Feature: dto-layer-implementation, Property {number}: {property_text}")`

### Test Organization

```
src/test/java/com/example/wasla/dto
├── request
│   ├── RegisterRequestTest.java (unit tests)
│   ├── RegisterRequestPropertyTest.java (property tests)
│   ├── LoginRequestTest.java
│   ├── LoginRequestPropertyTest.java
│   ├── CreateOrderRequestTest.java
│   ├── CreateOrderRequestPropertyTest.java
│   ├── UpdateOrderStatusRequestTest.java
│   ├── UpdateOrderStatusRequestPropertyTest.java
│   ├── UpdateDriverLocationRequestTest.java
│   ├── UpdateDriverLocationRequestPropertyTest.java
│   ├── CreateReviewRequestTest.java
│   └── CreateReviewRequestPropertyTest.java
└── response
    ├── AuthResponseTest.java
    ├── AuthResponsePropertyTest.java
    ├── OrderResponseTest.java
    ├── OrderResponsePropertyTest.java
    ├── OrderHistoryResponseTest.java
    ├── OrderHistoryResponsePropertyTest.java
    ├── AvailableOrdersResponseTest.java
    ├── AvailableOrdersResponsePropertyTest.java
    ├── ReviewResponseTest.java
    └── ReviewResponsePropertyTest.java
```

### Example Property Test

```java
@Tag("Feature: dto-layer-implementation, Property 1: RegisterRequest Username Validation")
class RegisterRequestUsernamePropertyTest {
    
    @Property(tries = 100)
    void usernameValidation(@ForAll("invalidUsernames") String username) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setPhone("+1234567890");
        request.setRole("CLIENT");
        
        Set<ConstraintViolation<RegisterRequest>> violations = 
            validator.validate(request);
        
        assertThat(violations)
            .isNotEmpty()
            .anyMatch(v -> v.getPropertyPath().toString().equals("username"));
    }
    
    @Provide
    Arbitrary<String> invalidUsernames() {
        return Arbitraries.oneOf(
            Arbitraries.just(null),
            Arbitraries.strings().ofMaxLength(2),
            Arbitraries.strings().ofMinLength(51)
        );
    }
}
```

### Unit Test Coverage

Unit tests should cover:
1. Valid DTO construction and validation
2. Invalid field values (null, empty, out of range)
3. Boundary values (min/max lengths, coordinate limits)
4. JSON serialization with Jackson ObjectMapper
5. JSON deserialization with valid/invalid JSON
6. Validation error messages
7. Lombok-generated methods (equals, hashCode, toString)

### Integration Testing

Integration tests should verify:
1. DTOs work correctly with Spring Boot controllers
2. `@Valid` annotation triggers validation
3. Validation errors are properly formatted in HTTP responses
4. JSON content negotiation works correctly
5. Error responses match expected format

### Test Data Generators

For property-based testing, create generators for:
- Valid/invalid email addresses
- Valid/invalid phone numbers
- Coordinate values (in-range and out-of-range)
- Strings of various lengths
- Valid/invalid enum values
- Timestamp values

Example generator:
```java
@Provide
Arbitrary<Double> validLatitudes() {
    return Arbitraries.doubles()
        .between(-90.0, 90.0);
}

@Provide
Arbitrary<Double> invalidLatitudes() {
    return Arbitraries.oneOf(
        Arbitraries.doubles().lessThan(-90.0),
        Arbitraries.doubles().greaterThan(90.0)
    );
}
```

### Continuous Testing

- Run unit tests on every commit
- Run property tests in CI/CD pipeline
- Monitor test execution time (property tests may be slower)
- Track code coverage (aim for >90% for DTO classes)
- Validate that all 24 properties have corresponding tests

