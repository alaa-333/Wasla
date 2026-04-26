package com.example.wasla.user.driver.entity;

import com.example.wasla.common.entity.BaseEntity;
import com.example.wasla.common.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

/**
 * Driver entity — an independent truck owner-operator.
 * Contains authentication, profile, and vehicle information.
 */
@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Driver extends BaseEntity implements User {

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 30)
    private VehicleType vehicleType;

    @Column(name = "license_plate", nullable = false, unique = true, length = 20)
    private String licensePlate;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = false;

    @Column(name = "current_lat", precision = 10, scale = 7)
    private BigDecimal currentLat;

    @Column(name = "current_lng", precision = 10, scale = 7)
    private BigDecimal currentLng;

    @Column(name = "rating_avg", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal ratingAvg = BigDecimal.ZERO;

    @Column(name = "total_jobs")
    @Builder.Default
    private Integer totalJobs = 0;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;
}
