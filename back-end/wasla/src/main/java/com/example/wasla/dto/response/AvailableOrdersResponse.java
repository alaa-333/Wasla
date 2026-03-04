package com.example.wasla.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Response DTO for available orders list.
 * Returns a list of orders with PENDING status that are available for drivers to accept.
 * This class is immutable - all fields are set via constructor and no setters are provided.
 */
@Getter
@AllArgsConstructor
public class AvailableOrdersResponse {
    
    /**
     * List of available orders with PENDING status
     */
    private final List<OrderResponse> orders;
}
