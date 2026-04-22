package com.example.wasla.location;

import com.example.wasla.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "location_history", indexes = {
    @Index(name = "idx_location_driver_id", columnList = "driver_id"),
    @Index(name = "idx_location_job_id", columnList = "job_id")
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
}
