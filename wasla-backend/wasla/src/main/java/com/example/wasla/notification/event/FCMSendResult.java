package com.example.wasla.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Value object wrapping the result of an FCM send attempt.
 * Used by NotificationOutboxDispatcher to decide: mark-sent, retry, or fail.
 */
@Getter
@AllArgsConstructor
public class FCMSendResult {

    private final boolean success;
    private final boolean retryable;
    private final String  messageId;
    private final String  reason;

    /** Successful send — stores the FCM message ID. */
    public static FCMSendResult success(String messageId) {
        return new FCMSendResult(true, false, messageId, null);
    }

    /** Temporary failure — dispatcher should schedule a retry. */
    public static FCMSendResult retryable(String reason) {
        return new FCMSendResult(false, true, null, reason);
    }

    /** Permanent failure — no more retries. */
    public static FCMSendResult failed(String reason) {
        return new FCMSendResult(false, false, null, reason);
    }
}
