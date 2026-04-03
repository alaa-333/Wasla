package com.example.wasla.notification.event;


import com.example.wasla.entity.NotificationOutbox;
import com.example.wasla.notification.repository.NotificationOutboxRepository;
import com.example.wasla.notification.service.NotificationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
public class NotificationOutboxDispatcher {


    private final NotificationOutboxRepository outboxRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;


    @Scheduled(fixedDelay = 5000)   // 5 seconds after last run completes
    @Transactional
    public void dispatch() {

        List<NotificationOutbox> batch =
                outboxRepository.findNextBatch(LocalDateTime.now());

        if (batch.isEmpty()) return;

        log.info("Outbox dispatch — processing {} notifications", batch.size());

        // Mark all as PROCESSING atomically before we start
        // Prevents double-dispatch if another instance also polls at the same time
        List<UUID> ids = batch.stream().map(NotificationOutbox::getId).toList();
        outboxRepository.markAsProcessing(ids);

        // Sync in-memory status with DB to prevent overwriting PROCESSING→PENDING on save
        batch.forEach(entry -> entry.setStatus("PROCESSING"));

        batch.forEach(this::processOne);
    }

    private void processOne(NotificationOutbox entry) {
        MDC.put("outboxId",  entry.getId().toString());
        MDC.put("eventType", entry.getEventType());
        MDC.put("userId",    entry.getRecipientUserId().toString());
        if (entry.getJobId() != null) {
            MDC.put("jobId", entry.getJobId().toString());
        }

        try {
            // 1. TTL check — discard expired notifications
            if (entry.isExpired()) {
                log.info("Notification expired — discarding. EventType: {}, Age: {}s",
                        entry.getEventType(), entry.getTtlSeconds());
                entry.markFailed("Expired — TTL of " + entry.getTtlSeconds() + "s exceeded");
                outboxRepository.save(entry);
                return;
            }

            // 2. Parse data payload
            Map<String, String> data = parseDataPayload(entry.getDataPayload());

            // 3. Dispatch via FCM
            FCMSendResult result;
            if (entry.isDataOnly()) {
                notificationService.sendSilentDataToUser(entry.getRecipientUserId(), data);
                result = FCMSendResult.success("silent");
            } else {
                result = notificationService.notifyUserWithResult(
                        entry.getRecipientUserId(),
                        entry.getTitle(),
                        entry.getBody(),
                        data
                );
            }

            // 4. Handle result
            if (result.isSuccess()) {
                entry.markSent(result.getMessageId());
                log.info("Notification dispatched — OutboxId: {}, MessageId: {}",
                        entry.getId(), result.getMessageId());

            } else if (result.isRetryable() && entry.hasRetriesLeft()) {
                entry.scheduleRetry(result.getReason());
                log.warn("Notification failed — will retry. OutboxId: {}, " +
                                "Attempt: {}/{}, NextRetry: {}",
                        entry.getId(),
                        entry.getRetryCount(),
                        entry.getMaxRetries(),
                        entry.getNextRetryAt());

            } else {
                // Non-retryable OR retries exhausted
                entry.markFailed(result.getReason());
                log.error("Notification permanently failed — OutboxId: {}, " +
                                "EventType: {}, UserId: {}, Reason: {}",
                        entry.getId(),
                        entry.getEventType(),
                        entry.getRecipientUserId(),
                        result.getReason());
            }

            outboxRepository.save(entry);

        } catch (Exception e) {
            log.error("Unexpected error dispatching OutboxId: {} — {}",
                    entry.getId(), e.getMessage(), e);

            if (entry.hasRetriesLeft()) {
                entry.scheduleRetry("Unexpected error: " + e.getMessage());
            } else {
                entry.markFailed("Unexpected error after max retries: " + e.getMessage());
            }
            outboxRepository.save(entry);

        } finally {
            MDC.clear();
        }
    }

    private Map<String, String> parseDataPayload(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(json,
                    new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse data payload — using empty map: {}", e.getMessage());
            return Map.of();
        }
    }

    /**
     * Cleanup job: delete SENT records older than 30 days.
     * Runs every Sunday at 3 AM. Keeps the table lean.
     */
    @Scheduled(cron = "0 0 3 * * SUN")
    @Transactional
    public void cleanupOldRecords() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        int deleted = outboxRepository.deleteOldSentRecords(cutoff);
        log.info("Outbox cleanup — deleted {} old SENT records (older than {})",
                deleted, cutoff.toLocalDate());
    }
}
