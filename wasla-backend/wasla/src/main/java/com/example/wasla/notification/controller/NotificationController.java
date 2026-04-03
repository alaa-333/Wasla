package com.example.wasla.notification.controller;


import com.example.wasla.notification.service.NotificationService;
import com.example.wasla.notification.dto.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService service;


    /**
     * POST /api/v1/notifications/send
     *
     * Test endpoint — used to manually trigger a notification.
     * In production, notifications are triggered by the outbox pattern, not direct API calls.
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendNotification(@RequestBody NotificationRequest request) {

        service.sendToDevice(
                request.getFcmToken(),
                request.getTitle(),
                request.getBody(),
                request.getData()
        );

        return ResponseEntity.ok(
                Map.of("status", "success")
        );
    }
}
