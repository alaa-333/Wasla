package com.example.wasla.job.repository;

import com.example.wasla.job.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

    Page<Job> findAllByClientId(UUID clientId, Pageable pageable);

    Page<Job> findAllByDriverId(UUID driverId, Pageable pageable);

    @EntityGraph(attributePaths = {"client", "driver"})
    Optional<Job> findByIdAndClientId(UUID jobId, UUID clientId);

    @Query("SELECT j FROM Job j WHERE j.status IN ('OPEN', 'BIDDING') AND j.expiresAt > :now")
    List<Job> findOpenJobs(@Param("now") LocalDateTime now);

    /**
     * Find jobs within a specified radius using PostGIS ST_DWithin.
     * Uses geography type for accurate distance calculation on Earth's surface.
     * 
     * @param lat Center point latitude
     * @param lng Center point longitude  
     * @param radiusMeters Search radius in meters
     * @param now Current timestamp for expiry check
     * @return List of jobs within radius, ordered by distance (nearest first)
     */
    @Query(value = """
        SELECT j.*, 
               ST_Distance(
                   ST_MakePoint(j.pickup_lng, j.pickup_lat)::geography,
                   ST_MakePoint(:lng, :lat)::geography
               ) as distance
        FROM jobs j
        WHERE j.status IN ('OPEN', 'BIDDING')
          AND j.expires_at > :now
          AND ST_DWithin(
              ST_MakePoint(j.pickup_lng, j.pickup_lat)::geography,
              ST_MakePoint(:lng, :lat)::geography,
              :radiusMeters
          )
        ORDER BY distance ASC
        """, 
        nativeQuery = true)
    List<Job> findNearbyJobs(
        @Param("lat") BigDecimal lat,
        @Param("lng") BigDecimal lng,
        @Param("radiusMeters") double radiusMeters,
        @Param("now") LocalDateTime now
    );

    /**
     * Find jobs within radius with distance calculation.
     * Returns jobs with computed distance for display purposes.
     * 
     * @param lat Center point latitude
     * @param lng Center point longitude
     * @param radiusMeters Search radius in meters
     * @param now Current timestamp
     * @return List of Object arrays: [Job entity, distance in meters]
     */
    @Query(value = """
        SELECT j, 
               ST_Distance(
                   ST_MakePoint(j.pickup_lng, j.pickup_lat)::geography,
                   ST_MakePoint(:lng, :lat)::geography
               ) as distance
        FROM jobs j
        WHERE j.status IN ('OPEN', 'BIDDING')
          AND j.expires_at > :now
          AND ST_DWithin(
              ST_MakePoint(j.pickup_lng, j.pickup_lat)::geography,
              ST_MakePoint(:lng, :lat)::geography,
              :radiusMeters
          )
        ORDER BY distance ASC
        """,
        nativeQuery = true)
    List<Object[]> findNearbyJobsWithDistance(
        @Param("lat") BigDecimal lat,
        @Param("lng") BigDecimal lng,
        @Param("radiusMeters") double radiusMeters,
        @Param("now") LocalDateTime now
    );

    /**
     * Count jobs within a specified radius.
     * Useful for analytics and driver availability checks.
     */
    @Query(value = """
        SELECT COUNT(*)
        FROM jobs j
        WHERE j.status IN ('OPEN', 'BIDDING')
          AND j.expires_at > :now
          AND ST_DWithin(
              ST_MakePoint(j.pickup_lng, j.pickup_lat)::geography,
              ST_MakePoint(:lng, :lat)::geography,
              :radiusMeters
          )
        """,
        nativeQuery = true)
    long countNearbyJobs(
        @Param("lat") BigDecimal lat,
        @Param("lng") BigDecimal lng,
        @Param("radiusMeters") double radiusMeters,
        @Param("now") LocalDateTime now
    );

    @Modifying
    @Query("UPDATE Job j SET j.status = 'EXPIRED' WHERE j.status IN ('OPEN', 'BIDDING') AND j.expiresAt < :now")
    int expireOldJobs(@Param("now") LocalDateTime now);
}
