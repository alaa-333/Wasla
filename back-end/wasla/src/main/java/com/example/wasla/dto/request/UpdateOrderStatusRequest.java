package com.example.wasla.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating order status.
 * Captures order status updates from drivers with validation constraints.
 * The status field is required and must not be null.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {

    @NotNull(message = "Status is required. Valid values are: " +
            "PENDING, " +
            "ACCEPTED, " +
            "PICKED_UP, " +
            "DELIVERED, " +
            "CANCELLED")
    private String status;
}
