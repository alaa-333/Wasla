package com.example.wasla.user.driver.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileDto {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String vehicleType;
    private String licensePlate;
    private String photoUrl;
    private Boolean isAvailable;
    private BigDecimal currentLat;
    private BigDecimal currentLng;
    private BigDecimal ratingAvg;
    private Integer totalJobs;
}
