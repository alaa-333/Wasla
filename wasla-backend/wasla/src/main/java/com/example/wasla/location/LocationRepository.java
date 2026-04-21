package com.example.wasla.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<LocationHistory, UUID> {
    
    // Get all locations for a job, ordered by timestamp (newest first)
    List<LocationHistory> findByJobIdOrderByTimestampDesc(UUID jobId);
    
    // Get latest location for a job
    Optional<LocationHistory> findFirstByJobIdOrderByTimestampDesc(UUID jobId);
    
    // Get locations for a driver within time range
    List<LocationHistory> findByDriverIdAndTimestampBetween(
        UUID driverId, 
        LocalDateTime start, 
        LocalDateTime end
    );
    
    // Count locations for a job
    long countByJobId(UUID jobId);
    
    // Delete old locations (for cleanup job)
    void deleteByTimestampBefore(LocalDateTime cutoffDate);
}
