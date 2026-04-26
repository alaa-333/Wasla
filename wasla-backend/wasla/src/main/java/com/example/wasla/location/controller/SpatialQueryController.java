package com.example.wasla.location.controller;

import com.example.wasla.common.dto.ApiResponse;
import com.example.wasla.job.entity.Job;
import com.example.wasla.location.service.SpatialQueryService;
import com.example.wasla.user.driver.entity.Driver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for PostGIS spatial queries.
 * Provides endpoints for location-based searches.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/spatial")
@RequiredArgsConstructor
@Tag(name = "Spatial Queries", description = "PostGIS spatial query endpoints")
public class SpatialQueryController {

    private final SpatialQueryService spatialQueryService;

    // ─── Job Spatial Queries ─────────────────────────────────────────────

    @GetMapping("/jobs/nearby")
    @PreAuthorize("hasAuthority('DRIVER')")
    @Operation(summary = "Find jobs within radius", 
               description = "Find all open jobs within specified radius using PostGIS ST_DWithin")
    public ResponseEntity<ApiResponse<List<Job>>> findJobsWithinRadius(
            @Parameter(description = "Center latitude") 
            @RequestParam BigDecimal lat,
            
            @Parameter(description = "Center longitude") 
            @RequestParam BigDecimal lng,
            
            @Parameter(description = "Search radius in kilometers (default: 15)") 
            @RequestParam(defaultValue = "15") BigDecimal radiusKm) {
        
        List<Job> jobs = spatialQueryService.findJobsWithinRadius(lat, lng, radiusKm);
        
        return ResponseEntity.ok(ApiResponse.ok(

            "Found " + jobs.size() + " jobs within " + radiusKm + " km",
                jobs
        ));
    }

    @GetMapping("/jobs/nearby/count")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'DRIVER')")
    @Operation(summary = "Count jobs within radius",
               description = "Count open jobs within specified radius")
    public ResponseEntity<ApiResponse<Map<String, Object>>> countJobsWithinRadius(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "15") BigDecimal radiusKm) {
        
        long count = spatialQueryService.countJobsWithinRadius(lat, lng, radiusKm);
        
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        data.put("radiusKm", radiusKm);
        data.put("centerLat", lat);
        data.put("centerLng", lng);
        
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    // ─── Driver Spatial Queries ───────────────────────────────────────────

    @GetMapping("/drivers/nearby")
    @PreAuthorize("hasAuthority('CLIENT')")
    @Operation(summary = "Find available drivers within radius",
               description = "Find all available drivers within specified radius")
    public ResponseEntity<ApiResponse<List<Driver>>> findDriversWithinRadius(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "10") BigDecimal radiusKm) {
        
        List<Driver> drivers = spatialQueryService.findAvailableDriversWithinRadius(lat, lng, radiusKm);
        
        return ResponseEntity.ok(ApiResponse.ok(

            "Found " + drivers.size() + " available drivers within " + radiusKm + " km",
                drivers
        ));
    }

    @GetMapping("/drivers/nearby/count")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'DRIVER')")
    @Operation(summary = "Count available drivers within radius",
               description = "Count available drivers within specified radius")
    public ResponseEntity<ApiResponse<Map<String, Object>>> countDriversWithinRadius(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "10") BigDecimal radiusKm) {
        
        long count = spatialQueryService.countAvailableDriversWithinRadius(lat, lng, radiusKm);
        
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        data.put("radiusKm", radiusKm);
        data.put("centerLat", lat);
        data.put("centerLng", lng);
        
        return ResponseEntity.ok(ApiResponse.ok(data));
    }


    // ─── Utility Endpoints ────────────────────────────────────────────────

    @GetMapping("/distance")
    @Operation(summary = "Calculate distance between two points",
               description = "Calculate distance in meters between two coordinates")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateDistance(
            @RequestParam BigDecimal lat1,
            @RequestParam BigDecimal lng1,
            @RequestParam BigDecimal lat2,
            @RequestParam BigDecimal lng2) {
        
        // Use Haversine formula (fallback if PostGIS not available)
        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double deltaLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double deltaLng = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceMeters = 6371000 * c; // Earth radius in meters
        
        Map<String, Object> data = new HashMap<>();
        data.put("distanceMeters", Math.round(distanceMeters));
        data.put("distanceKm", Math.round(distanceMeters / 1000.0 * 100.0) / 100.0);
        data.put("distanceMiles", Math.round(distanceMeters * 0.000621371 * 100.0) / 100.0);
        
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
