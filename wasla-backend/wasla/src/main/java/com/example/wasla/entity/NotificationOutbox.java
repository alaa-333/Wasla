package com.example.wasla.entity;


import com.example.wasla.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_outbox")
@Getter
@Setter
public class NotificationOutbox extends BaseEntity {


    @Column(name = "event_type",         nullable = false, length = 60)
    private String eventType;

    @Column(name = "recipient_user_id",  nullable = false)
    private UUID recipientUserId;

    @Column(name = "recipient_role",     nullable = false, length = 20)
    private String recipientRole;

    @Column(name = "title",              nullable = false)
    private String title;

    @Column(name = "body",               nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "data_payload",       columnDefinition = "JSONB")
    private String dataPayload;       // stored as JSON string, parsed when needed

    @Column(name = "job_id")
    private UUID jobId;

    @Column(name = "bid_id")
    private UUID bidId;

    @Column(name = "actor_user_id")
    private UUID actorUserId;

    @Column(name = "priority",           nullable = false, length = 10)
    private String priority;

    @Column(name = "ttl_seconds",        nullable = false)
    private int ttlSeconds;

    @Column(name = "data_only",          nullable = false)
    private boolean dataOnly;

    @Column(name = "status",             nullable = false, length = 20)
    private String status;

    @Column(name = "retry_count",        nullable = false)
    private int retryCount;

    @Column(name = "max_retries",        nullable = false)
    private int maxRetries;

    @Column(name = "next_retry_at",      nullable = false)
    private LocalDateTime nextRetryAt;

    @Column(name = "last_error",         columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "fcm_message_id")
    private String fcmMessageId;


    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    // ── Lifecycle helpers ────────────────────────────────────────────────────

    public void markSent(String messageId) {
        this.status       = "SENT";
        this.fcmMessageId = messageId;
        this.processedAt  = LocalDateTime.now();
        this.lastError    = null;
    }

    public void markFailed(String errorMessage) {
        this.status    = "FAILED";
        this.lastError = errorMessage;
        this.processedAt = LocalDateTime.now();
    }

    public void scheduleRetry(String errorMessage) {
        this.retryCount++;
        this.lastError  = errorMessage;
        this.status     = "PENDING";

        // Exponential backoff: 30s, 60s, 120s
        long delaySeconds = 30L * (1L << (this.retryCount - 1));
        this.nextRetryAt = LocalDateTime.now().plusSeconds(delaySeconds);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(
                this.createdAt.plusSeconds(this.ttlSeconds));
    }

    public boolean hasRetriesLeft() {
        return this.retryCount < this.maxRetries;
    }
}
