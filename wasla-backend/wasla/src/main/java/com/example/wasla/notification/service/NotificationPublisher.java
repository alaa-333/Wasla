package com.example.wasla.notification.service;

import com.example.wasla.notification.entity.WaslaEventType;
import com.example.wasla.notification.event.WaslaNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Helper service for publishing notification events.
 * Business services should use this instead of directly using ApplicationEventPublisher.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final ApplicationEventPublisher eventPublisher;

    // ─── Job Events ───────────────────────────────────────────────────────

    /**
     * Notify nearby drivers about a new job.
     * Called when a client creates a new job.
     */
    public void publishJobPosted(UUID jobId, UUID clientId, String jobTitle) {
        WaslaNotificationEvent event = WaslaNotificationEvent.builder(this)
                .eventType(WaslaEventType.JOB_POSTED)
                .recipientUserId(clientId)
                .recipientRole(WaslaNotificationEvent.RecipientRole.CUSTOMER)
                .title("Job Posted Successfully")
                .body("Your job '" + jobTitle + "' has been posted. Waiting for driver bids.")
                .jobId(jobId)
                .data(Map.of(
                        "jobId", jobId.toString(),
                        "type", "JOB_POSTED"
                ))
                .priority("NORMAL")
                .ttlSeconds(3600) // 1 hour
                .build();

        eventPublisher.publishEvent(event);
        log.info("Published JOB_POSTED event for job: {}", jobId);
    }

    /**
     * Notify client about a new bid on their job.
     * Called when a driver submits a bid.
     */
    public void publishBidPlaced(UUID jobId, UUID bidId, UUID clientId, 
                                  UUID driverId, String driverName, String price) {
        WaslaNotificationEvent event = WaslaNotificationEvent.builder(this)
                .eventType(WaslaEventType.BID_PLACED)
                .recipientUserId(clientId)
                .recipientRole(WaslaNotificationEvent.RecipientRole.CUSTOMER)
                .title("New Bid Received")
                .body(driverName + " placed a bid of $" + price + " on your job")
                .jobId(jobId)
                .bidId(bidId)
                .actorUserId(driverId)
                .data(Map.of(
                        "jobId", jobId.toString(),
                        "bidId", bidId.toString(),
                        "driverId", driverId.toString(),
                        "price", price,
                        "type", "BID_PLACED"
                ))
                .priority("HIGH")
                .ttlSeconds(1800) // 30 minutes
                .build();

        eventPublisher.publishEvent(event);
        log.info("Published BID_PLACED event for job: {}, bid: {}", jobId, bidId);
    }

    /**
     * Notify driver that their bid was accepted.
     * Called when client accepts a bid.
     */
    public void publishBidAccepted(UUID jobId, UUID bidId, UUID driverId, 
                                     UUID clientId, String jobTitle) {
        WaslaNotificationEvent event = WaslaNotificationEvent.builder(this)
                .eventType(WaslaEventType.BID_ACCEPTED)
                .recipientUserId(driverId)
                .recipientRole(WaslaNotificationEvent.RecipientRole.DRIVER)
                .title("Bid Accepted! 🎉")
                .body("Your bid on '" + jobTitle + "' was accepted. Contact the client to proceed.")
                .jobId(jobId)
                .bidId(bidId)
                .actorUserId(clientId)
                .data(Map.of(
                        "jobId", jobId.toString(),
                        "bidId", bidId.toString(),
                        "clientId", clientId.toString(),
                        "type", "BID_ACCEPTED"
                ))
                .priority("HIGH")
                .ttlSeconds(3600)
                .build();

        eventPublisher.publishEvent(event);
        log.info("Published BID_ACCEPTED event for driver: {}, job: {}", driverId, jobId);
    }

    /**
     * Notify drivers that their bids were rejected (bulk notification).
     * Called when client accepts one bid, rejecting all others.
     */
    public void publishBidRejected(UUID jobId, UUID driverId) {
        WaslaNotificationEvent event = WaslaNotificationEvent.builder(this)
                .eventType(WaslaEventType.BID_REJECTED_BULK)
                .recipientUserId(driverId)
                .recipientRole(WaslaNotificationEvent.RecipientRole.DRIVER)
                .title("Bid Update")
                .body("Another driver was selected for the job. Better luck next time!")
                .jobId(jobId)
                .data(Map.of(
                        "jobId", jobId.toString(),
                        "type", "BID_REJECTED"
                ))
                .priority("LOW")
                .ttlSeconds(1800)
                .build();

        eventPublisher.publishEvent(event);
        log.info("Published BID_REJECTED event for driver: {}", driverId);
    }

    // ─── Job Status Events ───────────────────────────────────────────────

    /**
     * Notify client that driver has started the job.
     * Called when driver updates job status to IN_PROGRESS.
     */
    public void publishJobStarted(UUID jobId, UUID clientId, UUID driverId, String driverName) {
        WaslaNotificationEvent event = WaslaNotificationEvent.builder(this)
                .eventType(WaslaEventType.JOB_STARTED)
                .recipientUserId(clientId)
                .recipientRole(WaslaNotificationEvent.RecipientRole.CUSTOMER)
                .title("Driver En Route")
                .body(driverName + " has started your job and is on the way!")
                .jobId(jobId)
                .actorUserId(driverId)
                .data(Map.of(
                        "jobId", jobId.toString(),
                        "driverId", driverId.toString(),
                        "type", "JOB_STARTED"
                ))
                .priority("HIGH")
                .ttlSeconds(1800)
                .build();

        eventPublisher.publishEvent(event);
        log.info("Published JOB_STARTED event for job: {}", jobId);
    }

    /**
     * Notify client that the job is completed.
     * Called when driver updates job status to COMPLETED.
     */
    public void publishJobCompleted(UUID jobId, UUID clientId, UUID driverId, String driverName) {
        WaslaNotificationEvent event = WaslaNotificationEvent.builder(this)
                .eventType(WaslaEventType.JOB_COMPLETED)
                .recipientUserId(clientId)
                .recipientRole(WaslaNotificationEvent.RecipientRole.CUSTOMER)
                .title("Job Completed! ✅")
                .body("Your job has been completed by " + driverName + ". Please rate your experience.")
                .jobId(jobId)
                .actorUserId(driverId)
                .data(Map.of(
                        "jobId", jobId.toString(),
                        "driverId", driverId.toString(),
                        "type", "JOB_COMPLETED"
                ))
                .priority("HIGH")
                .ttlSeconds(86400) // 24 hours
                .build();

        eventPublisher.publishEvent(event);
        log.info("Published JOB_COMPLETED event for job: {}", jobId);
    }

    /**
     * Notify driver that client cancelled the job.
     * Called when client cancels a job.
     */
    public void publishJobCancelledByCustomer(UUID jobId, UUID driverId, UUID clientId, String reason) {
        WaslaNotificationEvent event = WaslaNotificationEvent.builder(this)
                .eventType(WaslaEventType.JOB_CANCELLED_BY_CUSTOMER)
                .recipientUserId(driverId)
                .recipientRole(WaslaNotificationEvent.RecipientRole.DRIVER)
                .title("Job Cancelled")
                .body("The client has cancelled the job. Reason: " + (reason != null ? reason : "Not specified"))
                .jobId(jobId)
                .actorUserId(clientId)
                .data(Map.of(
                        "jobId", jobId.toString(),
                        "clientId", clientId.toString(),
                        "type", "JOB_CANCELLED"
                ))
                .priority("HIGH")
                .ttlSeconds(3600)
                .build();

        eventPublisher.publishEvent(event);
        log.info("Published JOB_CANCELLED_BY_CUSTOMER event for job: {}", jobId);
    }

    // ─── Rating Events ───────────────────────────────────────────────────

    /**
     * Notify driver that they received a rating.
     * Called when client submits a rating.
     */
    public void publishReviewReceived(UUID jobId, UUID driverId, UUID clientId, 
                                       int score, String comment) {
        WaslaNotificationEvent event = WaslaNotificationEvent.builder(this)
                .eventType(WaslaEventType.REVIEW_RECEIVED)
                .recipientUserId(driverId)
                .recipientRole(WaslaNotificationEvent.RecipientRole.DRIVER)
                .title("New Review Received ⭐")
                .body("You received a " + score + "-star rating!" + 
                      (comment != null ? " Comment: " + comment : ""))
                .jobId(jobId)
                .actorUserId(clientId)
                .data(Map.of(
                        "jobId", jobId.toString(),
                        "score", String.valueOf(score),
                        "type", "REVIEW_RECEIVED"
                ))
                .priority("NORMAL")
                .ttlSeconds(86400)
                .build();

        eventPublisher.publishEvent(event);
        log.info("Published REVIEW_RECEIVED event for driver: {}", driverId);
    }

    // ─── Location Events (Data-Only) ─────────────────────────────────────

    /**
     * Send silent location update to client.
     * Data-only notification - no visible alert.
     */
    public void publishDriverLocationUpdate(UUID jobId, UUID clientId, 
                                             double lat, double lng) {
        WaslaNotificationEvent event = WaslaNotificationEvent.builder(this)
                .eventType(WaslaEventType.DRIVER_LOCATION_UPDATE)
                .recipientUserId(clientId)
                .recipientRole(WaslaNotificationEvent.RecipientRole.CUSTOMER)
                .title("")  // Empty for data-only
                .body("")   // Empty for data-only
                .jobId(jobId)
                .data(Map.of(
                        "jobId", jobId.toString(),
                        "lat", String.valueOf(lat),
                        "lng", String.valueOf(lng),
                        "type", "LOCATION_UPDATE"
                ))
                .dataOnly(true)  // Silent notification
                .priority("NORMAL")
                .ttlSeconds(60) // 1 minute - location updates are time-sensitive
                .build();

        eventPublisher.publishEvent(event);
        log.debug("Published DRIVER_LOCATION_UPDATE event for job: {}", jobId);
    }
}
