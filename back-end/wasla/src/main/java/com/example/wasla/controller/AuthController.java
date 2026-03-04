package com.example.wasla.controller;

import com.example.wasla.dto.request.LoginRequest;
import com.example.wasla.dto.request.ClientRegisterRequest;
import com.example.wasla.dto.request.DriverRegisterRequest;
import com.example.wasla.dto.response.TokenResponse;
import com.example.wasla.util.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/client")
    public ResponseEntity<TokenResponse> registerClient(
            @RequestBody @Valid ClientRegisterRequest request) {
        var response = authService.registerClient(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/driver")
    public ResponseEntity<TokenResponse> registerDriver(
            @RequestBody @Valid DriverRegisterRequest request) {
        var response = authService.registerDriver(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody @Valid LoginRequest request) {

        var response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
