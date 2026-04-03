
package com.example.wasla.notification.service;

import com.example.wasla.notification.entity.DeviceType;
import com.example.wasla.notification.event.FCMSendResult;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Push notification service using Firebase Cloud Messaging.
 * Provides both fire-and-forget and result-aware send methods.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final DeviceTokenService deviceTokenService;

    // ─── Public API (called by business logic and outbox dispatcher) ─────

    /**
     * Sends a notification to ALL active devices of a given user.
     * Fire-and-forget — used from business logic where you don't need the result.
     */
    public void notifyUser(UUID userId, String title, String body, Map<String, String> data) {

        List<String> tokens = deviceTokenService.getActiveTokensForUser(userId);

        if (tokens.isEmpty()) {
            log.info("No active tokens for user {} — skipping notification", userId);
            return;
        }

        if (tokens.size() == 1) {
            sendToToken(tokens.get(0), title, body, data);
        } else {
            sendToMultipleTokens(tokens, title, body, data);
        }
    }

    /**
     * Sends a notification to a user and returns the result.
     * Used by {@link com.example.wasla.notification.event.NotificationOutboxDispatcher}
     * to decide: mark-sent, retry, or fail.
     */
    public FCMSendResult notifyUserWithResult(UUID userId, String title, String body,
                                               Map<String, String> data) {

        List<String> tokens = deviceTokenService.getActiveTokensForUser(userId);

        if (tokens.isEmpty()) {
            log.info("No active tokens for user {} — skipping notification", userId);
            return FCMSendResult.success("no-tokens");   // nothing to do — treat as success
        }

        // Send to first active token and return result
        String token = tokens.get(0);
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putAllData(data)
                .build();
        try {
            String messageId = FirebaseMessaging.getInstance().send(message);
            deviceTokenService.markTokenAsUsed(token);
            log.info("Notification sent — MessageId: {}", messageId);

            // If user has multiple devices, also send to remaining tokens (best-effort)
            if (tokens.size() > 1) {
                sendToMultipleTokens(tokens.subList(1, tokens.size()), title, body, data);
            }

            return FCMSendResult.success(messageId);

        } catch (FirebaseMessagingException e) {
            handleSendException(token, e);
            MessagingErrorCode code = e.getMessagingErrorCode();
            boolean retryable = code == MessagingErrorCode.UNAVAILABLE
                    || code == MessagingErrorCode.INTERNAL;
            return retryable
                    ? FCMSendResult.retryable(code + ": " + e.getMessage())
                    : FCMSendResult.failed(code + ": " + e.getMessage());
        }
    }

    /**
     * Sends a data-only (silent) notification to all devices of a user.
     * Used for background updates like driver location, etc.
     */
    public void sendSilentDataToUser(UUID userId, Map<String, String> data) {

        List<String> tokens = deviceTokenService.getActiveTokensForUser(userId);

        if (tokens.isEmpty()) {
            log.info("No active tokens for user {} — skipping silent data push", userId);
            return;
        }

        for (String token : tokens) {
            Message message = Message.builder()
                    .setToken(token)
                    .putAllData(data)
                    .build();
            try {
                String messageId = FirebaseMessaging.getInstance().send(message);
                deviceTokenService.markTokenAsUsed(token);
                log.info("Silent data sent — Token: {}..., MessageId: {}",
                        token.substring(0, Math.min(20, token.length())), messageId);
            } catch (FirebaseMessagingException e) {
                handleSendException(token, e);
            }
        }
    }

    /**
     * Sends a push notification to a specific device token.
     * Public so it can be used by the test controller endpoint.
     */
    public void sendToDevice(String token, String title, String body,
                             Map<String, String> data) {
        sendToToken(token, title, body, data);
    }


    /**
     * Sends to a specific platform only.
     * Use case: send Android-specific deep link that doesn't work on iOS.
     */
    public void notifyUserOnPlatform(UUID userId, DeviceType platform, String title,
                                     String body, Map<String, String> data) {

        List<String> tokens = deviceTokenService
                .getActiveTokensForUserAndPlatform(userId, platform);

        if (tokens.isEmpty()) {
            log.info("No active {} tokens for user {} — skipping", platform, userId);
            return;
        }
        sendToMultipleTokens(tokens, title, body, data);
    }


    /**
     * Sends to multiple users at once.
     * Use case: notify all members of an order group, all drivers in a zone, etc.
     */
    public void notifyUsers(List<UUID> userIds, String title, String body,
                            Map<String, String> data) {

        List<String> tokens = deviceTokenService.getActiveTokensForUsers(userIds);

        if (tokens.isEmpty()) {
            log.info("No active tokens found for {} users — skipping", userIds.size());
            return;
        }

        // Auto-partition into batches of 500
        for (int i = 0; i < tokens.size(); i += 500) {
            List<String> batch = tokens.subList(i, Math.min(i + 500, tokens.size()));
            sendToMultipleTokens(batch, title, body, data);
        }
    }

    // ─── Topic Management ───────────────────────────────────────────────────

    public void subscribeToTopic(String token, String topic) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .subscribeToTopic(List.of(token), topic);

            log.info("Topic subscription — Topic: {}, Success: {}, Failure: {}",
                    topic, response.getSuccessCount(), response.getFailureCount());

        } catch (FirebaseMessagingException e) {
            log.error("Failed to subscribe token to topic {}: {}", topic, e.getMessage());
            throw new RuntimeException("Topic subscribe failed", e);
        }
    }

    public void unsubscribeFromTopic(String token, String topic) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(List.of(token), topic);

            log.info("Topic unsubscription — Topic: {}, Success: {}, Failure: {}",
                    topic,
                    response.getSuccessCount(),
                    response.getFailureCount());

        } catch (FirebaseMessagingException e) {
            log.error("Failed to unsubscribe from topic {}: {}", topic, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String sendToTopic(String topic, String title, String body, Map<String, String> data) {

        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                        .setBody(body)
                        .setTitle(title)
                        .build())
                .putAllData(data)
                .build();

        try {
            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("Topic message sent — Topic: {}, MessageId: {}", topic, messageId);
            return messageId;
        } catch (FirebaseMessagingException e) {
            log.error("Topic message send failed — Topic: {}, Error: {}", topic, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // ─── Internal helpers ───────────────────────────────────────────────────

    private void sendToToken(String token, String title, String body,
                             Map<String, String> data) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putAllData(data)
                .build();
        try {
            String messageId = FirebaseMessaging.getInstance().send(message);
            deviceTokenService.markTokenAsUsed(token);
            log.info("Notification sent — MessageId: {}", messageId);
        } catch (FirebaseMessagingException e) {
            handleSendException(token, e);
        }
    }

    private void sendToMultipleTokens(List<String> tokens, String title,
                                      String body, Map<String, String> data) {
        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putAllData(data)
                .build();
        try {
            BatchResponse response = FirebaseMessaging.getInstance()
                    .sendEachForMulticast(message);

            log.info("Multicast sent — Success: {}, Failure: {}",
                    response.getSuccessCount(), response.getFailureCount());

            processFailedTokens(tokens, response);

        } catch (FirebaseMessagingException e) {
            log.error("Multicast failed: {}", e.getMessage());
        }
    }

    private void handleSendException(String token, FirebaseMessagingException e) {
        MessagingErrorCode code = e.getMessagingErrorCode();
        if (code == MessagingErrorCode.UNREGISTERED ||
                code == MessagingErrorCode.INVALID_ARGUMENT) {
            deviceTokenService.deactivateToken(token);
        }
        log.error("FCM send error — Code: {}, Message: {}", code, e.getMessage());
    }

    private void processFailedTokens(List<String> tokens, BatchResponse response) {
        List<SendResponse> responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
                MessagingErrorCode code = responses.get(i)
                        .getException().getMessagingErrorCode();
                if (code == MessagingErrorCode.UNREGISTERED ||
                        code == MessagingErrorCode.INVALID_ARGUMENT) {
                    deviceTokenService.deactivateToken(tokens.get(i));
                }
            }
        }
    }
}
