package com.example.wasla.location;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LocationMessageUpdate {

    @NotBlank(message = "Job ID is required")
    private String jobId;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private BigDecimal lat;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private BigDecimal lng;

    @DecimalMin(value = "0.0", message = "Speed cannot be negative")
    private BigDecimal speed;      // km/h

    @DecimalMin(value = "0.0", message = "Accuracy cannot be negative")
    private BigDecimal accuracy;   // meters

    @DecimalMin(value = "0.0", message = "Heading must be between 0 and 360")
    @DecimalMax(value = "360.0", message = "Heading must be between 0 and 360")
    private BigDecimal heading;    // degrees

    private Long timestamp;
}
