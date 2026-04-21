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
                .speed(messageUpdate.getSpeed())
                .accuracy(messageUpdate.getAccuracy())
                .heading(messageUpdate.getHeading())
                .timestamp(LocalDateTime.now())
                .build();

        locationRepository.save(location);
        log.debug("Saved location for driver {} at ({}, {})", 
                driverId, messageUpdate.getLat(), messageUpdate.getLng());
    }

    public RouteDto getJobRoute(UUID jobId) {
        List<LocationHistory> locations = locationRepository
                .findByJobIdOrderByTimestampDesc(jobId);

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
        return locationRepository.findFirstByJobIdOrderByTimestampDesc(jobId)
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
     * Calculate distance between two points using Haversine formula
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
                .speed(location.getSpeed())
                .heading(location.getHeading())
                .timestamp(location.getTimestamp())
                .build();
    }
}
