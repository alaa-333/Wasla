package com.example.wasla.job.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobRequest {

    @NotBlank(message = "Pickup address is required")
    @Size(max = 255, message = "Pickup address must not exceed 255 characters")
    private String pickupAddress;

    @NotNull(message = "Pickup latitude is required")
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private BigDecimal pickupLat; // Changed from Double to BigDecimal

    @NotNull(message = "Pickup longitude is required")
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private BigDecimal pickupLng; // Changed from Double to BigDecimal

    @NotBlank(message = "Dropoff address is required")
    @Size(max = 255, message = "Dropoff address must not exceed 255 characters")
    private String dropoffAddress;

    @NotNull(message = "Dropoff latitude is required")
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private BigDecimal dropoffLat; // Changed from Double to BigDecimal

    @NotNull(message = "Dropoff longitude is required")
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private BigDecimal dropoffLng; // Changed from Double to BigDecimal

    @Size(max = 1000, message = "Cargo description must not exceed 1000 characters")
    private String cargoDesc;

    private String cargoPhotoUrl;
}
