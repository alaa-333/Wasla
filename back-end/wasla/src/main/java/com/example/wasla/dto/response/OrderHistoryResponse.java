package com.example.wasla.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Response DTO for user's order history.
 * Returns a list of orders for a specific user.
 * This class is immutable - all fields are set via constructor and no setters are provided.
 */
@Getter
@AllArgsConstructor
public class OrderHistoryResponse {
    
    /**
     * List of orders belonging to the user
     */
    private final List<OrderResponse> orders;
}
