package com.example.wasla.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for order information.
 * Returns complete order details including client info, driver info (nullable), 
 * addresses, coordinates, pricing, and timestamps.
 * This class is immutable - all fields are set via constructor and no setters are provided.
 */
@Getter
@AllArgsConstructor
public class OrderResponse {
    
    /**
     * Order's unique identifier
     */
    private final Long id;
    
    /**
     * Client user's unique identifier
     */
    private final Long clientId;
    
    /**
     * Client user's username
     */
    private final String clientName;
    
    /**
     * Driver user's unique identifier (nullable - null when no driver assigned)
     */
    private final Long driverId;
    
    /**
     * Driver user's username (nullable - null when no driver assigned)
     */
    private final String driverName;
    
    /**
     * Order status (e.g., PENDING, ACCEPTED, IN_PROGRESS, DELIVERED, CANCELLED)
     */
    private final String status;
    
    /**
     * Pickup location address
     */
    private final String pickupAddress;
    
    /**
     * Delivery location address
     */
    private final String dropAddress;
    
    /**
     * Pickup location latitude
     */
    private final Double pickupLat;
    
    /**
     * Pickup location longitude
     */
    private final Double pickupLng;
    
    /**
     * Drop-off location latitude
     */
    private final Double dropLat;
    
    /**
     * Drop-off location longitude
     */
    private final Double dropLng;
    
    /**
     * Order price
     */
    private final BigDecimal price;
    
    /**
     * Package weight in kg
     */
    private final Double weight;
    
    /**
     * Order creation timestamp
     */
    private final LocalDateTime createdDate;
    
    /**
     * Last modification timestamp
     */
    private final LocalDateTime lastModifiedDate;
}
