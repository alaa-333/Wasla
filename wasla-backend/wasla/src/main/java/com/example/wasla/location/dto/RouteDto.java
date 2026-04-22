package com.example.wasla.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteDto {

    private UUID jobId;
    private List<LocationPoint> points;
    private BigDecimal totalDistance;  // km
    private Integer pointCount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LocationPoint {
        private BigDecimal latitude;
        private BigDecimal longitude;
        private LocalDateTime timestamp;
    }
}
