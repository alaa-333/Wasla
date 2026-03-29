package com.example.wasla.job.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobListDto {
    private String id; // Changed from Long to String (UUID)
    private String pickupAddress;
    private String dropoffAddress;
    private BigDecimal pickupLat; // Changed from Double to BigDecimal
    private BigDecimal pickupLng; // Changed from Double to BigDecimal
    private BigDecimal dropoffLat; // Changed from Double to BigDecimal
    private BigDecimal dropoffLng; // Changed from Double to BigDecimal
    private String cargoDesc;
    private String status;
    private BigDecimal acceptedPrice;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private int bidCount;
}
