package com.example.wasla.controller;

import com.example.wasla.dto.request.LoginRequest;
import com.example.wasla.dto.request.ClientRegisterRequest;
import com.example.wasla.dto.request.DriverRegisterRequest;
import com.example.wasla.dto.response.TokenResponse;
import com.example.wasla.util.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and authorization")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new client", description = "Creates a new client account and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    @PostMapping("/register/client")
    public ResponseEntity<TokenResponse> registerClient(
            @RequestBody @Valid ClientRegisterRequest request) {
        var response = authService.registerClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Register a new driver", description = "Creates a new driver account and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Driver registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    @PostMapping("/register/driver")
    public ResponseEntity<TokenResponse> registerDriver(
            @RequestBody @Valid DriverRegisterRequest request) {
        var response = authService.registerDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody @Valid LoginRequest request) {

        var response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
