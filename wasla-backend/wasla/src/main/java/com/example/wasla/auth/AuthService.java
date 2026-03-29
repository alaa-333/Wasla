package com.example.wasla.auth;

import com.example.wasla.auth.dto.*;
import com.example.wasla.auth.entity.RefreshToken;
import com.example.wasla.auth.repository.RefreshTokenRepository;
import com.example.wasla.auth.security.JwtService;
import com.example.wasla.common.exception.ErrorCode;
import com.example.wasla.common.exception.WaslaAppException;
import com.example.wasla.user.client.entity.Client;
import com.example.wasla.user.client.repository.ClientRepository;
import com.example.wasla.user.driver.entity.Driver;
import com.example.wasla.user.driver.entity.VehicleType;
import com.example.wasla.user.driver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Authentication service handling registration, login, token refresh, and logout.
 * Uses Email + Password authentication with JWT tokens.
 * Works with separate Client and Driver entities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientRepository clientRepository;
    private final DriverRepository driverRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new client user.
     */
    @Transactional
    public AuthResponse registerClient(ClientRegisterRequest request) {
        checkEmailNotTaken(request.getEmail());

        Client client = Client.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .build();

        client = clientRepository.save(client);
        log.info("New client registered: {}", client.getEmail());
        
        return buildAuthResponse(client.getId(), client.getEmail(), "CLIENT", true);
    }

    /**
     * Register a new driver user with vehicle profile.
     */
    @Transactional
    public AuthResponse registerDriver(DriverRegisterRequest request) {
        checkEmailNotTaken(request.getEmail());

        Driver driver = Driver.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .vehicleType(VehicleType.valueOf(request.getVehicleType()))
                .licensePlate(request.getLicensePlate())
                .isAvailable(false)
                .build();

        driver = driverRepository.save(driver);
        log.info("New driver registered: {}", driver.getEmail());
        
        return buildAuthResponse(driver.getId(), driver.getEmail(), "DRIVER", true);
    }

    /**
     * Authenticate with email and password.
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Try to find as client first
        Client client = clientRepository.findByEmail(request.getEmail()).orElse(null);
        if (client != null) {
            if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
                throw new WaslaAppException(ErrorCode.WRONG_CREDENTIALS);
            }
            log.info("Client logged in: {}", client.getEmail());
            return buildAuthResponse(client.getId(), client.getEmail(), "CLIENT", false);
        }

        // Try to find as driver
        Driver driver = driverRepository.findByEmail(request.getEmail()).orElse(null);
        if (driver != null) {
            if (!passwordEncoder.matches(request.getPassword(), driver.getPassword())) {
                throw new WaslaAppException(ErrorCode.WRONG_CREDENTIALS);
            }
            log.info("Driver logged in: {}", driver.getEmail());
            return buildAuthResponse(driver.getId(), driver.getEmail(), "DRIVER", false);
        }

        throw new WaslaAppException(ErrorCode.USER_NOT_FOUND);
    }

    /**
     * Refresh access token using a valid refresh token.
     * Implements token rotation — old refresh token is deleted, new pair is issued.
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new WaslaAppException(ErrorCode.INVALID_REFRESH_TOKEN));

        // Check expiry
        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new WaslaAppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Validate JWT signature
        if (!jwtService.validateToken(request.getRefreshToken())) {
            refreshTokenRepository.delete(storedToken);
            throw new WaslaAppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Token rotation: delete old, issue new
        refreshTokenRepository.delete(storedToken);

        // Get user email based on role
        String email;
        if ("CLIENT".equals(storedToken.getUserRole())) {
            Client client = clientRepository.findById(storedToken.getUserId())
                    .orElseThrow(() -> new WaslaAppException(ErrorCode.USER_NOT_FOUND));
            email = client.getEmail();
        } else {
            Driver driver = driverRepository.findById(storedToken.getUserId())
                    .orElseThrow(() -> new WaslaAppException(ErrorCode.USER_NOT_FOUND));
            email = driver.getEmail();
        }

        log.info("Token refreshed for user: {}", email);
        return buildAuthResponse(storedToken.getUserId(), email, storedToken.getUserRole(), false);
    }

    /**
     * Logout by invalidating the refresh token.
     */
    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);
        log.info("User logged out");
    }

    // ── Helpers ──

    private void checkEmailNotTaken(String email) {
        if (clientRepository.existsByEmail(email) || driverRepository.existsByEmail(email)) {
            throw new WaslaAppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private AuthResponse buildAuthResponse(UUID userId, String email, String role, boolean isNewUser) {
        // Create custom UserDetails for JWT generation
        org.springframework.security.core.userdetails.User userDetails = 
            new org.springframework.security.core.userdetails.User(
                email, 
                "", 
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role))
            );

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Persist refresh token
        RefreshToken rt = RefreshToken.builder()
                .userId(userId)
                .userRole(role)
                .token(refreshToken)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();
        refreshTokenRepository.save(rt);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpiration() / 1000) // seconds
                .user(AuthResponse.UserInfo.builder()
                        .id(userId.toString())
                        .email(email)
                        .role(role)
                        .newUser(isNewUser)
                        .build())
                .build();
    }
}
