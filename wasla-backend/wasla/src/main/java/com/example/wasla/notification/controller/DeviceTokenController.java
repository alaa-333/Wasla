package com.example.wasla.notification.controller;


import com.example.wasla.common.dto.ApiResponse;
import com.example.wasla.notification.service.DeviceTokenService;
import com.example.wasla.notification.dto.DeviceRegistrationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceTokenController {

    private final DeviceTokenService tokenService;


    /**
     * POST /api/v1/devices/register
     *
     * Registers or refreshes an FCM token for the authenticated user.
     * Called by mobile app on:
     *   - Login
     *   - App startup (if token may have changed)
     *   - onTokenRefresh callback from Firebase SDK
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody DeviceRegistrationRequest request
    ) {

        tokenService.registerToken(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Device token registered successfully"));
    }



    /**
     * DELETE /api/v1/devices/{token}
     *
     * Removes a specific device token.
     * Called when user explicitly logs out from one device.
     */
    @DeleteMapping("/{token}")
    public ResponseEntity<ApiResponse> removeToken(
            @AuthenticationPrincipal UUID userId,
            @PathVariable("token") String token
    ) {

        tokenService.deactivateToken(token);
        return ResponseEntity.ok(ApiResponse.ok("Device token removed"));

    }



    /**
     * DELETE /api/v1/devices
     *
     * Removes ALL device tokens for the authenticated user.
     * Called on full logout — user should receive no more notifications.
     */
    @DeleteMapping
    public  ResponseEntity<ApiResponse> removeAllTokens(
            @AuthenticationPrincipal UUID userId
    ) {

        tokenService.deactivateAllTokensForUser(userId);
        return ResponseEntity.ok(ApiResponse.ok("All device tokens removed"));


    }

}
