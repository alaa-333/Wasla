package com.example.wasla.model.enums;

/**
 * Enumeration representing the lifecycle states of an order in the Wasla logistics application.
 * Defines the five possible states an order can be in from creation to completion.
 */
public enum OrderStatus {
    /**
     * Pending status - order has been created but not yet accepted by a driver
     */
    PENDING,
    
    /**
     * Accepted status - order has been accepted by a driver
     */
    ACCEPTED,
    
    /**
     * Picked up status - driver has picked up the package from the pickup location
     */
    PICKED_UP,
    
    /**
     * Delivered status - order has been successfully delivered to the drop-off location
     */
    DELIVERED,
    
    /**
     * Cancelled status - order has been cancelled and will not be fulfilled
     */
    CANCELLED
}
