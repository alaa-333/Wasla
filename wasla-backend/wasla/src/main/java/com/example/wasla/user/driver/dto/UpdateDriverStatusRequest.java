package com.example.wasla.user.driver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for PATCH /api/v1/drivers/me/status
 * Per docs v2.0: { online: true/false }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDriverStatusRequest {

    @NotNull(message = "Online status is required")
    private Boolean online;
}
