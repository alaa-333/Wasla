package com.example.wasla.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new order with pickup and drop-off locations.
 * Includes validation for addresses, coordinates, and package weight.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotBlank(message = "Pickup address is required and must not be blank")
    @Size(max = 255, message = "Pickup address must not exceed 255 characters")
    private String pickupAddress;

    @NotBlank(message = "Drop address is required and must not be blank")
    @Size(max = 255, message = "Drop address must not exceed 255 characters")
    private String dropAddress;

    @NotNull(message = "Pickup latitude is required")
    @Min(value = -90, message = "Pickup latitude must be between -90 and 90")
    @Max(value = 90, message = "Pickup latitude must be between -90 and 90")
    private Double pickupLat;

    @NotNull(message = "Pickup longitude is required")
    @Min(value = -180, message = "Pickup longitude must be between -180 and 180")
    @Max(value = 180, message = "Pickup longitude must be between -180 and 180")
    private Double pickupLng;

    @NotNull(message = "Drop latitude is required")
    @Min(value = -90, message = "Drop latitude must be between -90 and 90")
    @Max(value = 90, message = "Drop latitude must be between -90 and 90")
    private Double dropLat;

    @NotNull(message = "Drop longitude is required")
    @Min(value = -180, message = "Drop longitude must be between -180 and 180")
    @Max(value = 180, message = "Drop longitude must be between -180 and 180")
    private Double dropLng;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be greater than 0")
    private Double weight;
}
