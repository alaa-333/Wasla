package com.example.wasla.location.service;

import com.example.wasla.job.entity.Job;
import com.example.wasla.job.repository.JobRepository;
import com.example.wasla.user.driver.entity.Driver;
import com.example.wasla.user.driver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for PostGIS spatial queries.
 * Provides advanced geospatial operations for location-based features.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpatialQueryService {

    private final JobRepository jobRepository;
    private final DriverRepository driverRepository;

    // ─── Job Spatial Queries ─────────────────────────────────────────────

    /**
     * Find jobs within a specified radius of a location.
     * Uses PostGIS ST_DWithin for accurate distance calculation.
     * 
     * @param lat Center latitude
     * @param lng Center longitude
     * @param radiusKm Search radius in kilometers
     * @return List of jobs ordered by distance (nearest first)
     */
    @Transactional(readOnly = true)
    public List<Job> findJobsWithinRadius(BigDecimal lat, BigDecimal lng, BigDecimal radiusKm) {
        double radiusMeters = radiusKm.multiply(BigDecimal.valueOf(1000)).doubleValue();
        
        List<Job> jobs = jobRepository.findNearbyJobs(lat, lng, radiusMeters, LocalDateTime.now());
        
        log.info("Found {} jobs within {} km of ({}, {})", jobs.size(), radiusKm, lat, lng);
        
        return jobs;
    }

    /**
     * Find jobs within radius with distance information.
     * Returns jobs with computed distance for display.
     * 
     * @param lat Center latitude
     * @param lng Center longitude
     * @param radiusKm Search radius in kilometers
     * @return List of Object arrays: [Job, distance in meters]
     */
    @Transactional(readOnly = true)
    public List<Object[]> findJobsWithDistance(BigDecimal lat, BigDecimal lng, BigDecimal radiusKm) {
        double radiusMeters = radiusKm.multiply(BigDecimal.valueOf(1000)).doubleValue();
        
        return jobRepository.findNearbyJobsWithDistance(lat, lng, radiusMeters, LocalDateTime.now());
    }

    /**
     * Count jobs within a specified radius.
     * Useful for analytics and heat maps.
     * 
     * @param lat Center latitude
     * @param lng Center longitude
     * @param radiusKm Search radius in kilometers
     * @return Number of jobs within radius
     */
    @Transactional(readOnly = true)
    public long countJobsWithinRadius(BigDecimal lat, BigDecimal lng, BigDecimal radiusKm) {
        double radiusMeters = radiusKm.multiply(BigDecimal.valueOf(1000)).doubleValue();
        
        return jobRepository.countNearbyJobs(lat, lng, radiusMeters, LocalDateTime.now());
    }

    // ─── Driver Spatial Queries ───────────────────────────────────────────

    /**
     * Find available drivers within a specified radius.
     * Used for job notifications and driver discovery.
     * 
     * @param lat Center latitude
     * @param lng Center longitude
     * @param radiusKm Search radius in kilometers
     * @return List of available drivers ordered by distance
     */
    @Transactional(readOnly = true)
    public List<Driver> findAvailableDriversWithinRadius(BigDecimal lat, BigDecimal lng, 
                                                          BigDecimal radiusKm) {
        double radiusMeters = radiusKm.multiply(BigDecimal.valueOf(1000)).doubleValue();
        
        List<Driver> drivers = driverRepository.findAvailableDriversNearby(lat, lng, radiusMeters);
        
        log.info("Found {} available drivers within {} km of ({}, {})", 
                drivers.size(), radiusKm, lat, lng);
        
        return drivers;
    }

    /**
     * Find available drivers with distance information.
     * Returns drivers with computed distance for ranking.
     * 
     * @param lat Center latitude
     * @param lng Center longitude
     * @param radiusKm Search radius in kilometers
     * @return List of Object arrays: [Driver, distance in meters]
     */
    @Transactional(readOnly = true)
    public List<Object[]> findAvailableDriversWithDistance(BigDecimal lat, BigDecimal lng,
                                                            BigDecimal radiusKm) {
        double radiusMeters = radiusKm.multiply(BigDecimal.valueOf(1000)).doubleValue();
        
        return driverRepository.findAvailableDriversNearbyWithDistance(lat, lng, radiusMeters);
    }

    /**
     * Count available drivers within a specified radius.
     * Useful for analytics and supply/demand matching.
     * 
     * @param lat Center latitude
     * @param lng Center longitude
     * @param radiusKm Search radius in kilometers
     * @return Number of available drivers within radius
     */
    @Transactional(readOnly = true)
    public long countAvailableDriversWithinRadius(BigDecimal lat, BigDecimal lng, 
                                                   BigDecimal radiusKm) {
        double radiusMeters = radiusKm.multiply(BigDecimal.valueOf(1000)).doubleValue();
        
        return driverRepository.countAvailableDriversNearby(lat, lng, radiusMeters);
    }

    // ─── Utility Methods ──────────────────────────────────────────────────

    /**
     * Convert kilometers to meters.
     */
    public double kmToMeters(BigDecimal km) {
        return km.multiply(BigDecimal.valueOf(1000)).doubleValue();
    }

    /**
     * Convert meters to kilometers.
     */
    public BigDecimal metersToKm(double meters) {
        return BigDecimal.valueOf(meters / 1000.0).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Convert meters to miles.
     */
    public BigDecimal metersToMiles(double meters) {
        return BigDecimal.valueOf(meters * 0.000621371).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Get driver IDs within radius for notification targeting.
     * Efficient method for bulk notifications.
     * 
     * @param lat Center latitude
     * @param lng Center longitude
     * @param radiusKm Search radius in kilometers
     * @return List of driver UUIDs
     */
    @Transactional(readOnly = true)
    public List<UUID> getDriverIdsWithinRadius(BigDecimal lat, BigDecimal lng, BigDecimal radiusKm) {
        return findAvailableDriversWithinRadius(lat, lng, radiusKm)
                .stream()
                .map(Driver::getId)
                .toList();
    }
}
