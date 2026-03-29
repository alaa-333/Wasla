package com.example.wasla.job.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponseDto {
    private String id; // Changed from Long to String (UUID)
    private String clientId; // Changed from Long to String (UUID)
    private String clientName;
    private String clientPhone;
    private String driverId; // Changed from Long to String (UUID)
    private String driverName;
    private String driverPhone;
    private String pickupAddress;
    private BigDecimal pickupLat; // Changed from Double to BigDecimal
    private BigDecimal pickupLng; // Changed from Double to BigDecimal
    private String dropoffAddress;
    private BigDecimal dropoffLat; // Changed from Double to BigDecimal
    private BigDecimal dropoffLng; // Changed from Double to BigDecimal
    private String cargoDesc;
    private String cargoPhotoUrl;
    private String status;
    private BigDecimal acceptedPrice;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private int bidCount;
}
