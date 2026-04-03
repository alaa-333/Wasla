package com.example.wasla.notification.repository;

import com.example.wasla.entity.NotificationOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox, UUID> {

    /**
     * Fetch the next batch of PENDING notifications ready for dispatch.
     *
     * Ordered by: priority (HIGH first), then created_at (oldest first).
     * Limit: 50 per poll — prevents overwhelming FCM in a single batch.
     *
     * next_retry_at <= now ensures exponential backoff is respected.
     */
    @Query(value = """
        SELECT * FROM notification_outbox
        WHERE status = 'PENDING'
          AND next_retry_at <= :now
        ORDER BY
            CASE priority
                WHEN 'HIGH'   THEN 1
                WHEN 'NORMAL' THEN 2
                WHEN 'LOW'    THEN 3
            END,
            created_at ASC
        LIMIT 50
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<NotificationOutbox> findNextBatch(@Param("now") LocalDateTime now);

    // Mark as PROCESSING to prevent double-dispatch in multi-instance deployments
    @Modifying
    @Query("UPDATE NotificationOutbox n SET n.status = 'PROCESSING' " +
            "WHERE n.id IN :ids")
    void markAsProcessing(@Param("ids") List<UUID> ids);

    // Cleanup: delete SENT records older than 30 days
    @Modifying
    @Query("DELETE FROM NotificationOutbox n " +
            "WHERE n.status = 'SENT' AND n.createdAt < :cutoff")
    int deleteOldSentRecords(@Param("cutoff") LocalDateTime cutoff);
}
