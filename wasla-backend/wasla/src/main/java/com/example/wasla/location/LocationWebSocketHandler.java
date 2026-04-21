package com.example.wasla.location;

import com.example.wasla.auth.security.SecurityHelper;
import com.example.wasla.common.exception.ErrorCode;
import com.example.wasla.common.exception.WaslaAppException;
import com.example.wasla.job.repository.JobRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

/**
 * WebSocket handler for real-time driver location updates.
 * Driver sends to: /app/driver.location
 * Client receives at: /user/queue/location
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class LocationWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final SecurityHelper securityHelper;
    private final JobRepository jobRepository;
    private final LocationService locationService;

    /**
     * Receives driver location updates and sends to specific client.
     * Destination: /app/driver.location
     */
    @MessageMapping("/driver.location")
    public void handleLocationUpdate(@Payload @Valid LocationMessageUpdate locationMessage) {

        try {
            // Get driver id from security context
            var driverId = securityHelper.getCurrentUserId();

            log.debug("Location from driver {}: ({}, {})",
                    driverId, locationMessage.getLat(), locationMessage.getLng());

            // Verify job exists
            var job = jobRepository.findById(UUID.fromString(locationMessage.getJobId()))
                    .orElseThrow(() -> new WaslaAppException(ErrorCode.JOB_NOT_FOUND));

            // Verify driver ownership
            if (!job.getDriver().getId().equals(driverId)) {
                log.warn("Driver {} tried to send location for job {} (not assigned)", 
                        driverId, locationMessage.getJobId());
                throw new WaslaAppException(ErrorCode.ACCESS_DENIED);
            }

            // Save in database
            locationService.saveLocationUpdate(driverId, locationMessage);

            // Get client id to send location update
            String clientId = job.getClient().getId().toString();

            // Send to specific client via private queue
            messagingTemplate.convertAndSendToUser(
                    clientId,
                    "/queue/location",
                    locationMessage
            );

            log.debug("Sent location to client: {}", clientId);

        } catch (WaslaAppException e) {
            log.error("Error handling location update: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error handling location update", e);
            throw new WaslaAppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
