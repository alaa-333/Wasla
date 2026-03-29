package com.example.wasla.job.entity;

import com.example.wasla.common.entity.BaseEntity;
import com.example.wasla.user.driver.entity.Driver;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Bid entity — a driver's price quote on a job.
 * Constraint: one bid per driver per job.
 */
@Builder
@Entity
@Table(name = "bids",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_bid_job_driver",
                columnNames = {"job_id", "driver_id"}),
        indexes = {
                @Index(name = "idx_bids_job_id", columnList = "job_id"),
                @Index(name = "idx_bids_driver_id", columnList = "driver_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bid extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "note")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private BidStatus status = BidStatus.PENDING;
}
