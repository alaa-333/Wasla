package com.example.wasla.notification.event;

import com.example.wasla.entity.NotificationOutbox;
import com.example.wasla.notification.repository.NotificationOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class NotificationOutboxWriter {

    private final NotificationOutboxRepository outboxRepository;
    private final ObjectMapper                 objectMapper;


    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    // Runs within the existing transaction from the business operation
    public void onNotificationEvent(WaslaNotificationEvent event) {
        try {
            String dataJson = objectMapper.writeValueAsString(event.getData());

            NotificationOutbox outbox = new NotificationOutbox();
            outbox.setEventType(event.getEventType().name());
            outbox.setRecipientUserId(event.getRecipientUserId());
            outbox.setRecipientRole(event.getRecipientRole().name());
            outbox.setTitle(event.getTitle());
            outbox.setBody(event.getBody());
            outbox.setDataPayload(dataJson);
            outbox.setJobId(event.getJobId());
            outbox.setBidId(event.getBidId());
            outbox.setActorUserId(event.getActorUserId());
            outbox.setPriority(event.getPriority());
            outbox.setTtlSeconds((int) event.getTtlSeconds());
            outbox.setDataOnly(event.isDataOnly());
            outbox.setStatus("PENDING");
            outbox.setRetryCount(0);
            outbox.setMaxRetries(3);
            outbox.setNextRetryAt(LocalDateTime.now());
            outbox.setCreatedAt(LocalDateTime.now());

            outboxRepository.save(outbox);

            log.info("Outbox entry created — EventType: {}, RecipientId: {}, JobId: {}",
                    event.getEventType(), event.getRecipientUserId(), event.getJobId());

        } catch (JsonProcessingException e) {
            // This should never happen with a Map<String, String>
            // If it does, it's a programming error — fail loudly
            throw new IllegalStateException(
                    "Failed to serialize notification data payload", e);
        }
    }

}
