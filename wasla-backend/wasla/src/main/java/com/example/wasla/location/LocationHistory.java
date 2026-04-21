package com.example.wasla.location;

import com.example.wasla.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "location_history", indexes = {
    @Index(name = "idx_location_driver_id", columnList = "driver_id"),
    @Index(name = "idx_location_job_id", columnList = "job_id"),
    @Index(name = "idx_location_timestamp", columnList = "timestamp"),
    @Index(name = "idx_location_job_timestamp", columnList = "job_id, timestamp")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class LocationHistory extends BaseEntity {

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal lat;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal lng;

    @Column(precision = 10, scale = 2)
    private BigDecimal speed;      // km/h

    @Column(precision = 10, scale = 2)
    private BigDecimal accuracy;   // meters

    @Column(precision = 10, scale = 2)
    private BigDecimal heading;    // degrees (0-360)

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
