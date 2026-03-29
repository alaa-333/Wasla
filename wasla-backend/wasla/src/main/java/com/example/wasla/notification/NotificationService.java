package com.example.wasla.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Notification service stub.
 * When FCM is fully integrated, this will send push notifications
 * for events like: new bid, bid accepted, job status change, etc.
 */
@Slf4j
@Service
public class NotificationService {

    /**
     * Send a push notification. Currently logs to console.
     * TODO: Integrate with Firebase Cloud Messaging (FCM).
     */
    public void sendNotification(String fcmToken, String title, String body) {
        log.info("📱 NOTIFICATION → token={}, title='{}', body='{}'", fcmToken, title, body);
    }

    /**
     * Send notification to a user by their userId.
     * TODO: Look up FCM token and send via FCM.
     */
    public void notifyUser(Long userId, String title, String body) {
        log.info("📱 NOTIFICATION → userId={}, title='{}', body='{}'", userId, title, body);
    }
}
