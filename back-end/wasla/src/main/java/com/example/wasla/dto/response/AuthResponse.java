package com.example.wasla.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response DTO for authentication operations (login and registration).
 * Returns JWT token and user information.
 * This class is immutable - all fields are set via constructor and no setters are provided.
 */
@Getter
@AllArgsConstructor
public class AuthResponse {
    
    /**
     * JWT authentication token for API access
     */
    private final String token;
    
    /**
     * User's unique identifier
     */
    private final Long userId;
    
    /**
     * User's fullName
     */
    private final String fullName;
    
    /**
     * User's email address
     */
    private final String email;
    
    /**
     * User's phone number
     */
    private final String phone;
    
    /**
     * User's role (CLIENT or DRIVER)
     */
    private final String role;
}
