package com.example.wasla.user.client.controller;

import com.example.wasla.auth.security.SecurityHelper;
import com.example.wasla.common.dto.ApiResponse;
import com.example.wasla.user.client.dto.ClientProfileDto;
import com.example.wasla.user.client.dto.UpdateClientProfileRequest;
import com.example.wasla.user.client.entity.Client;
import com.example.wasla.user.client.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Client profile management")
public class ClientController {

    private final ClientService clientService;
    private final SecurityHelper securityHelper;

    @Operation(summary = "Get my client profile")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ClientProfileDto>> getMyProfile() {
        Client client = securityHelper.getCurrentClient();
        ClientProfileDto dto = toDto(client);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @Operation(summary = "Update my profile")
    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponse<ClientProfileDto>> updateProfile(
            @RequestBody @Valid UpdateClientProfileRequest request) {
        Client client = securityHelper.getCurrentClient();
        Client updated = clientService.updateProfile(
                client.getId(),
                request.getFullName(),
                request.getPhone()
        );
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", toDto(updated)));
    }

    @Operation(summary = "Update FCM token")
    @PutMapping("/me/fcm-token")
    public ResponseEntity<ApiResponse<Void>> updateFcmToken(@RequestBody String fcmToken) {
        Client client = securityHelper.getCurrentClient();
        clientService.updateFcmToken(client.getId(), fcmToken);
        return ResponseEntity.ok(ApiResponse.ok("FCM token updated", null));
    }

    // Helper method
    private ClientProfileDto toDto(Client client) {
        return ClientProfileDto.builder()
                .id(client.getId().toString())
                .fullName(client.getFullName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .build();
    }
}
