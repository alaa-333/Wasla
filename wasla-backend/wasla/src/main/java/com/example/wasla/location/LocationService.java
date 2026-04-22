package com.example.wasla.location;

import com.example.wasla.common.exception.ErrorCode;
import com.example.wasla.common.exception.WaslaAppException;
import com.example.wasla.location.dto.RouteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional
    public void saveLocationUpdate(UUID driverId, LocationMessageUpdate messageUpdate) {
        // Validate coordinates
        validateCoordinates(messageUpdate.getLat(), messageUpdate.getLng());

        var location = LocationHistory.builder()
                .driverId(driverId)
                .jobId(UUID.fromString(messageUpdate.getJobId()))
                .lat(messageUpdate.getLat())
                .lng(messageUpdate.getLng())
                .build();

        locationRepository.save(location);
        log.debug("Saved location for driver {} at ({}, {})", 
                driverId, messageUpdate.getLat(), messageUpdate.getLng());
    }

    public RouteDto getJobRoute(UUID jobId) {
        List<LocationHistory> locations = locationRepository
                .findByJobIdOrderByCreatedAtDesc(jobId);

        if (locations.isEmpty()) {
            throw new WaslaAppException(ErrorCode.LOCATION_NOT_FOUND);
        }

        BigDecimal totalDistance = calculateTotalDistance(locations);

        return RouteDto.builder()
                .jobId(jobId)
                .points(locations.stream()
                        .map(this::toLocationPoint)
                        .collect(Collectors.toList()))
                .totalDistance(totalDistance)
                .pointCount(locations.size())
                .build();
    }

    public LocationHistory getLatestLocation(UUID jobId) {
        return locationRepository.findFirstByJobIdOrderByCreatedAtDesc(jobId)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.LOCATION_NOT_FOUND));
    }

    private BigDecimal calculateTotalDistance(List<LocationHistory> locations) {
        if (locations.size() < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDistance = BigDecimal.ZERO;

        for (int i = 0; i < locations.size() - 1; i++) {
            LocationHistory current = locations.get(i);
            LocationHistory next = locations.get(i + 1);

            double distance = calculateDistance(
                    current.getLat().doubleValue(),
                    current.getLng().doubleValue(),
                    next.getLat().doubleValue(),
                    next.getLng().doubleValue()
            );

            totalDistance = totalDistance.add(BigDecimal.valueOf(distance));
        }

        return totalDistance.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate distance between two points using Haversine formula.
     * This is a fallback for when PostGIS is not available.
     * For production, prefer using PostGIS ST_Distance for better accuracy.
     * 
     * @return distance in kilometers
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Earth radius in km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    /**
     * Calculate distance between two coordinates in meters.
     * Uses the Haversine formula for accurate Earth-surface distance.
     * 
     * @param lat1 First point latitude
     * @param lng1 First point longitude
     * @param lat2 Second point latitude
     * @param lng2 Second point longitude
     * @return Distance in meters
     */
    public double calculateDistanceInMeters(BigDecimal lat1, BigDecimal lng1, 
                                             BigDecimal lat2, BigDecimal lng2) {
        double distanceKm = calculateDistance(
            lat1.doubleValue(), 
            lng1.doubleValue(),
            lat2.doubleValue(), 
            lng2.doubleValue()
        );
        return distanceKm * 1000; // Convert to meters
    }

    /**
     * Check if a point is within a specified radius of another point.
     * Useful for client-side validation before making API calls.
     * 
     * @param centerLat Center point latitude
     * @param centerLng Center point longitude
     * @param pointLat Point to check latitude
     * @param pointLng Point to check longitude
     * @param radiusMeters Radius in meters
     * @return true if point is within radius
     */
    public boolean isWithinRadius(BigDecimal centerLat, BigDecimal centerLng,
                                   BigDecimal pointLat, BigDecimal pointLng,
                                   double radiusMeters) {
        double distanceMeters = calculateDistanceInMeters(centerLat, centerLng, pointLat, pointLng);
        return distanceMeters <= radiusMeters;
    }

    private void validateCoordinates(BigDecimal lat, BigDecimal lng) {
        if (lat.compareTo(new BigDecimal("-90")) < 0 || 
            lat.compareTo(new BigDecimal("90")) > 0) {
            throw new WaslaAppException(ErrorCode.INVALID_COORDINATES);
        }

        if (lng.compareTo(new BigDecimal("-180")) < 0 || 
            lng.compareTo(new BigDecimal("180")) > 0) {
            throw new WaslaAppException(ErrorCode.INVALID_COORDINATES);
        }
    }

    private RouteDto.LocationPoint toLocationPoint(LocationHistory location) {
        return RouteDto.LocationPoint.builder()
                .latitude(location.getLat())
                .longitude(location.getLng())
                .timestamp(location.getCreatedAt())
                .build();
    }
}
