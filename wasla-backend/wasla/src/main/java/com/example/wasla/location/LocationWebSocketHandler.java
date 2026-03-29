package com.example.wasla.location;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

/**
 * WebSocket handler for real-time driver location updates.
 * Per docs Section 4.3 and 7.6:
 *   Driver sends to: /app/driver.location
 *   Client subscribes to: /topic/job/{jobId}/location
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class LocationWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Receives driver location updates and broadcasts to subscribers.
     * Destination: /app/driver.location (matching docs Section 7.6)
     */
    @MessageMapping("/driver.location")
    public void handleLocationUpdate(@Payload LocationMessage message) {
        log.debug("Location update from driver {}: ({}, {})",
                message.getDriverId(), message.getLat(), message.getLng());

        // Broadcast to clients tracking this job
        if (message.getJobId() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/job/" + message.getJobId() + "/location",
                    message);
        }
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class LocationMessage {
        private String driverId; // Changed from Long to String (UUID)
        private String jobId; // Changed from Long to String (UUID)
        private BigDecimal lat; // Changed from Double to BigDecimal
        private BigDecimal lng; // Changed from Double to BigDecimal
        private Long timestamp;
    }
}
