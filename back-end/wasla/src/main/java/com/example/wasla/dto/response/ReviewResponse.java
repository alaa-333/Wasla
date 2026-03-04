package com.example.wasla.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Response DTO for review operations.
 * Returns review information including rating, comment, and timestamps.
 * This class is immutable - all fields are set via constructor and no setters are provided.
 */
@Getter
@AllArgsConstructor
public class ReviewResponse {
    
    /**
     * Review's unique identifier
     */
    private final Long id;
    
    /**
     * ID of the order being reviewed
     */
    private final Long orderId;
    
    /**
     * Rating value (1-5)
     */
    private final Integer rating;
    
    /**
     * Optional review comment (nullable)
     */
    private final String comment;
    
    /**
     * Review creation timestamp
     */
    private final LocalDateTime createdDate;
}
