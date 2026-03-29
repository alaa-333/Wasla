package com.example.wasla.auth;

import com.example.wasla.auth.dto.*;
import com.example.wasla.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints: registration, login, token refresh, logout.
 * All endpoints are public (no JWT required).
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration, login, token management")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new client")
    @PostMapping("/register/client")
    public ResponseEntity<ApiResponse<AuthResponse>> registerClient(
            @RequestBody @Valid ClientRegisterRequest request) {
        var response = authService.registerClient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Client registered successfully", response));
    }

    @Operation(summary = "Register a new driver")
    @PostMapping("/register/driver")
    public ResponseEntity<ApiResponse<AuthResponse>> registerDriver(
            @RequestBody @Valid DriverRegisterRequest request) {
        var response = authService.registerDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Driver registered successfully", response));
    }

    @Operation(summary = "Login with email and password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @RequestBody @Valid LoginRequest request) {
        var response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @Operation(summary = "Refresh access token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestBody @Valid RefreshTokenRequest request) {
        var response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.ok("Token refreshed", response));
    }

    @Operation(summary = "Logout (invalidate refresh token)")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody @Valid RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
    }
}
