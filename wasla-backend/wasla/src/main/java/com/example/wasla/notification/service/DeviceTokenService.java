package com.example.wasla.notification.service;


import com.example.wasla.notification.dto.DeviceRegistrationRequest;
import com.example.wasla.notification.entity.DeviceToken;
import com.example.wasla.notification.entity.DeviceType;
import com.example.wasla.notification.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceTokenService {

    private final DeviceTokenRepository tokenRepository;


    // ── Register / Refresh Token (Upsert) ───────────────────────────────────

    /**
     * Registers a new device token or updates an existing one.
     *
     * This is an UPSERT operation:
     * - If the token already exists in DB → update metadata
     * - If the token is new for this user → insert it
     * - If the token belongs to another user (rotated) → reassign to current user
     *
     * Called when:
     * 1. User logs in and the app sends its current token
     * 2. Firebase rotates the token (onTokenRefresh callback)
     * 3. App is reinstalled (new token issued)
     */
    @Transactional
    public DeviceToken registerToken(UUID userId, DeviceRegistrationRequest request) {

        Optional<DeviceToken> existingToken = tokenRepository.findByToken(request.getToken());

        if (existingToken.isPresent()) {

            var deviceToken = existingToken.get();

            deviceToken.setToken(request.getToken());
            deviceToken.setDeviceType(request.getDeviceType());
            deviceToken.setUserId(userId);
            deviceToken.setActive(true);

           var savedDeviceToken =  tokenRepository.save(deviceToken);

            log.info("Device token updated — UserId: {}, TokenPrefix: {}...",
                    userId, request.getToken().substring(0, Math.min(20, request.getToken().length())));

            return savedDeviceToken;
        }

        DeviceToken newDeviceToken =  DeviceToken.builder()
                .token(request.getToken())
                .deviceType(request.getDeviceType())
                .active(true)
                .userId(userId)
                .build();

        var savedDeviceToken =  tokenRepository.save(newDeviceToken);

        log.info("Device token registered — UserId: {}, TokenPrefix: {}...",
                userId, request.getToken().substring(0, Math.min(20, request.getToken().length())));

        return savedDeviceToken;
    }


    // ── Get Active Tokens ────────────────────────────────────────────────────
    /**
     * Returns all active FCM token strings for a given user.
     * This is what NotificationService calls before sending a notification.
     */
    @Transactional(readOnly = true)
    public List<String> getActiveTokensForUser(UUID userId) {

        List<DeviceToken> deviceTokens = tokenRepository
                .findByUserIdAndActiveTrue(userId);

        return deviceTokens.stream()
                .map(DeviceToken::getToken)
                .collect(Collectors.toList());
    }


    /**
     * Returns active tokens for a user, filtered by platform.
     * Use case: send Android-specific notification with custom channel ID.
     */
    @Transactional(readOnly = true)
    public List<String> getActiveTokensForUserAndPlatform(UUID userId, DeviceType deviceType) {
        return tokenRepository.findByUserIdAndDeviceTypeAndActiveTrue(userId, deviceType)
                .stream()
                .map(DeviceToken::getToken)
                .collect(Collectors.toList());
    }



    /**
     * Returns all active tokens for a list of users.
     * Use case: notify all members of a group, all users in a region, etc.
     */
    @Transactional(readOnly = true)
    public List<String> getActiveTokensForUsers(List<UUID> userIds) {
        return tokenRepository.findActiveTokensByUserIds(userIds)
                .stream()
                .map(DeviceToken::getToken)
                .collect(Collectors.toList());
    }


    // ── Deactivate / Remove Tokens ───────────────────────────────────────────

    /**
     * Deactivates a single token.
     * Called when FCM returns UNREGISTERED for this token,
     * or when the user explicitly removes a device.
     */
    @Transactional
    public void deactivateToken(String token) {
        int updated = tokenRepository.deactivateByToken(token, LocalDateTime.now());
        if (updated > 0) {
            log.info("Token deactivated — TokenPrefix: {}...",
                    token.substring(0, Math.min(20, token.length())));
        } else {
            log.warn("Attempted to deactivate unknown token — TokenPrefix: {}...",
                    token.substring(0, Math.min(20, token.length())));
        }
    }


    /**
     * Deactivates ALL tokens for a user.
     * Called on logout — user should not receive notifications on any device.
     */
    @Transactional
    public void deactivateAllTokensForUser(UUID userId) {
        int count = tokenRepository.deactivateByUserId(userId, LocalDateTime.now());
        log.info("All tokens deactivated for user — UserId: {}, Count: {}", userId, count);
    }


    /**
     * Updates last_used_at timestamp after a successful notification send.
     * Used to identify stale tokens in the cleanup job.
     */
    @Transactional
    public void markTokenAsUsed(String token) {
        tokenRepository.updateLastUsedAt(token, LocalDateTime.now());
    }
}
