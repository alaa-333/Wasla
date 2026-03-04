package com.example.wasla.model.enums;

/**
 * Enumeration representing user roles in the Wasla logistics application.
 * Defines the three types of users that can interact with the system.
 */
public enum Role {
    /**
     * Client role - users who request delivery services
     */
    CLIENT,
    
    /**
     * Driver role - users who fulfill delivery orders
     */
    DRIVER,
    
    /**
     * Admin role - users with administrative privileges
     */
    ADMIN
}
