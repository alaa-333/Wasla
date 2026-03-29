package com.example.wasla.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Authentication response returned after registration, login, or token refresh.
 * Matches the API contract defined in TECHNICAL_DOCUMENTATION.md Section 7.1
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    @Builder.Default
    private String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private UserInfo user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String id; // Changed from Long to String (UUID)
        private String email;
        private String phone;
        private String role;
        private boolean newUser;
    }
}
