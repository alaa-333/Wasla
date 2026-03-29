package com.example.wasla.user.driver.controller;

import com.example.wasla.auth.security.SecurityHelper;
import com.example.wasla.common.dto.ApiResponse;
import com.example.wasla.job.dto.BidResponseDto;
import com.example.wasla.job.service.BidService;
import com.example.wasla.user.driver.dto.*;
import com.example.wasla.user.driver.entity.Driver;
import com.example.wasla.user.driver.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Tag(name = "Drivers", description = "Driver profile management")
public class DriverController {

    private final DriverService driverService;
    private final BidService bidService;
    private final SecurityHelper securityHelper;

    @Operation(summary = "Get my driver profile")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<DriverProfileDto>> getMyProfile() {
        Driver driver = securityHelper.getCurrentDriver();
        DriverProfileDto dto = toDto(driver);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @Operation(summary = "Get driver profile by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverProfileDto>> getDriverById(@PathVariable UUID id) {
        Driver driver = driverService.findById(id);
        DriverProfileDto dto = toPublicDto(driver);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @Operation(summary = "Update my profile")
    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponse<DriverProfileDto>> updateProfile(
            @RequestBody @Valid UpdateDriverProfileRequest request) {
        Driver driver = securityHelper.getCurrentDriver();
        Driver updated = driverService.updateProfile(
                driver.getId(),
                request.getFullName(),
                request.getPhone(),
                request.getPhotoUrl()
        );
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", toDto(updated)));
    }

    @Operation(summary = "Update availability status")
    @PatchMapping("/me/status")
    public ResponseEntity<ApiResponse<DriverProfileDto>> updateAvailability(
            @RequestBody @Valid UpdateAvailabilityRequest request) {
        Driver driver = securityHelper.getCurrentDriver();
        Driver updated = driverService.updateAvailability(driver.getId(), request.getIsAvailable());
        return ResponseEntity.ok(ApiResponse.ok("Availability updated", toDto(updated)));
    }

    @Operation(summary = "Update current location")
    @PutMapping("/me/location")
    public ResponseEntity<ApiResponse<Void>> updateLocation(
            @RequestBody @Valid UpdateLocationRequest request) {
        Driver driver = securityHelper.getCurrentDriver();
        driverService.updateLocation(driver.getId(), request.getLat(), request.getLng());
        return ResponseEntity.ok(ApiResponse.ok("Location updated", null));
    }

    @Operation(summary = "Update FCM token")
    @PutMapping("/me/fcm-token")
    public ResponseEntity<ApiResponse<Void>> updateFcmToken(@RequestBody String fcmToken) {
        Driver driver = securityHelper.getCurrentDriver();
        driverService.updateFcmToken(driver.getId(), fcmToken);
        return ResponseEntity.ok(ApiResponse.ok("FCM token updated", null));
    }

    @Operation(summary = "Get my bids")
    @GetMapping("/me/bids")
    public ResponseEntity<ApiResponse<List<BidResponseDto>>> getMyBids() {
        Driver driver = securityHelper.getCurrentDriver();
        List<BidResponseDto> bids = bidService.getMyBids(driver.getId());
        return ResponseEntity.ok(ApiResponse.ok(bids));
    }

    // Helper methods
    private DriverProfileDto toDto(Driver driver) {
        return DriverProfileDto.builder()
                .id(driver.getId().toString())
                .fullName(driver.getFullName())
                .email(driver.getEmail())
                .phone(driver.getPhone())
                .vehicleType(driver.getVehicleType() != null ? driver.getVehicleType().name() : null)
                .licensePlate(driver.getLicensePlate())
                .photoUrl(driver.getPhotoUrl())
                .isAvailable(driver.getIsAvailable())
                .currentLat(driver.getCurrentLat())
                .currentLng(driver.getCurrentLng())
                .ratingAvg(driver.getRatingAvg())
                .totalJobs(driver.getTotalJobs())
                .build();
    }

    private DriverProfileDto toPublicDto(Driver driver) {
        // Public profile - hide sensitive info
        return DriverProfileDto.builder()
                .id(driver.getId().toString())
                .fullName(driver.getFullName())
                .vehicleType(driver.getVehicleType() != null ? driver.getVehicleType().name() : null)
                .photoUrl(driver.getPhotoUrl())
                .ratingAvg(driver.getRatingAvg())
                .totalJobs(driver.getTotalJobs())
                .build();
    }
}
