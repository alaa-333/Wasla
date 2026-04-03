package com.example.wasla.notification.dto;

import com.example.wasla.notification.entity.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRegistrationRequest {

    @NotBlank(message = "FCM token is required")
    @Size(min = 100, max = 512, message = "Invalid token length")
    private String token;

    @NotNull(message = "Device type is required")
    private DeviceType deviceType;   // ANDROID, IOS, WEB
}
