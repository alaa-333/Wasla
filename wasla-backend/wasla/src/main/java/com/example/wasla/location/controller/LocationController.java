package com.example.wasla.location.controller;

import com.example.wasla.common.dto.ApiResponse;
import com.example.wasla.location.LocationHistory;
import com.example.wasla.location.LocationService;
import com.example.wasla.location.dto.RouteDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Tag(name = "Location", description = "GPS location tracking APIs")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/jobs/{jobId}/route")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'DRIVER')")
    @Operation(summary = "Get job route history", 
               description = "Retrieve all GPS points for a specific job")
    public ResponseEntity<ApiResponse<RouteDto>> getJobRoute(@PathVariable UUID jobId) {
        RouteDto route = locationService.getJobRoute(jobId);
        return ResponseEntity.ok(ApiResponse.ok(route));
    }

    @GetMapping("/jobs/{jobId}/latest")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'DRIVER')")
    @Operation(summary = "Get latest location", 
               description = "Get the most recent location for a job")
    public ResponseEntity<ApiResponse<LocationHistory>> getLatestLocation(@PathVariable UUID jobId) {
        LocationHistory location = locationService.getLatestLocation(jobId);
        return ResponseEntity.ok(ApiResponse.ok(location));
    }
}
