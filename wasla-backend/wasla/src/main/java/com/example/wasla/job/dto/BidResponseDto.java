package com.example.wasla.job.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidResponseDto {
    private String id; // Changed from Long to String (UUID)
    private String jobId; // Changed from Long to String (UUID)
    private BigDecimal price;
    private String note;
    private String status;
    private LocalDateTime createdAt;
    private DriverSummary driver;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverSummary {
        private String id; // Changed from Long to String (UUID)
        private String name;
        private String vehicleType;
        private BigDecimal ratingAvg; // Changed from Double to BigDecimal
        private Integer totalJobs;
    }
}
