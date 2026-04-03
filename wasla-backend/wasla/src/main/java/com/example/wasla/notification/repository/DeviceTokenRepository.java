package com.example.wasla.notification.repository;

import com.example.wasla.notification.entity.DeviceToken;
import com.example.wasla.notification.entity.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {

    // Find a token by its string value
    Optional<DeviceToken> findByToken(String token);

    // All active tokens for a user (all devices)
    List<DeviceToken> findByUserIdAndActiveTrue(UUID userId);

    // Active tokens for a user filtered by platform
    List<DeviceToken> findByUserIdAndDeviceTypeAndActiveTrue(UUID userId, DeviceType deviceType);

    // All active tokens for multiple users (batch notification)
    @Query("SELECT dt FROM DeviceToken dt WHERE dt.userId IN :userIds AND dt.active = true")
    List<DeviceToken> findActiveTokensByUserIds(@Param("userIds") List<UUID> userIds);

    @Modifying
    @Query("DELETE FROM DeviceToken dt WHERE dt.token IN :tokens AND dt.active = true")
    void deleteOldTokens(@Param("tokens") List<String> tokens);

    // Check if a token already exists for a user
    boolean existsByUserIdAndToken(UUID userId, String token);

    // Soft-delete a specific token
    @Modifying
    @Query("UPDATE DeviceToken dt SET dt.active = false, dt.updatedAt = :now WHERE dt.token = :token")
    int deactivateByToken(@Param("token") String token, @Param("now") LocalDateTime now);

    // Soft-delete ALL tokens for a user (on logout)
    @Modifying
    @Query("UPDATE DeviceToken dt SET dt.active = false, dt.updatedAt = :now WHERE dt.userId = :userId AND dt.active = true")
    int deactivateByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    // Find stale tokens (not used in X days) — for cleanup job
    @Query("SELECT dt FROM DeviceToken dt WHERE dt.active = true AND (dt.lastUsedAt IS NULL OR dt.lastUsedAt < :cutoff)")
    List<DeviceToken> findOldTokens(@Param("cutoff") LocalDateTime cutoff);

    // Update last_used_at after successful send
    @Modifying
    @Query("UPDATE DeviceToken dt SET dt.lastUsedAt = :now WHERE dt.token = :token")
    void updateLastUsedAt(@Param("token") String token, @Param("now") LocalDateTime now);
}
