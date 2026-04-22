package com.example.wasla.user.driver.repository;

import com.example.wasla.user.driver.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {

    Optional<Driver> findByEmail(String email);

    boolean existsByEmail(String email);

    /**
     * Find available drivers within a specified radius using PostGIS ST_DWithin.
     * Used for job notifications and driver discovery.
     * 
     * @param lat Center point latitude
     * @param lng Center point longitude
     * @param radiusMeters Search radius in meters
     * @return List of available drivers within radius, ordered by distance
     */
    @Query(value = """
        SELECT d.*, 
               ST_Distance(
                   ST_MakePoint(d.current_lng, d.current_lat)::geography,
                   ST_MakePoint(:lng, :lat)::geography
               ) as distance
        FROM drivers d
        WHERE d.is_available = true
          AND d.current_lat IS NOT NULL
          AND d.current_lng IS NOT NULL
          AND ST_DWithin(
              ST_MakePoint(d.current_lng, d.current_lat)::geography,
              ST_MakePoint(:lng, :lat)::geography,
              :radiusMeters
          )
        ORDER BY distance ASC
        """,
        nativeQuery = true)
    List<Driver> findAvailableDriversNearby(
        @Param("lat") BigDecimal lat,
        @Param("lng") BigDecimal lng,
        @Param("radiusMeters") double radiusMeters
    );

    /**
     * Find available drivers within radius with their distance.
     * Returns drivers with computed distance for ranking.
     * 
     * @param lat Center point latitude
     * @param lng Center point longitude
     * @param radiusMeters Search radius in meters
     * @return List of Object arrays: [Driver entity, distance in meters]
     */
    @Query(value = """
        SELECT d, 
               ST_Distance(
                   ST_MakePoint(d.current_lng, d.current_lat)::geography,
                   ST_MakePoint(:lng, :lat)::geography
               ) as distance
        FROM drivers d
        WHERE d.is_available = true
          AND d.current_lat IS NOT NULL
          AND d.current_lng IS NOT NULL
          AND ST_DWithin(
              ST_MakePoint(d.current_lng, d.current_lat)::geography,
              ST_MakePoint(:lng, :lat)::geography,
              :radiusMeters
          )
        ORDER BY distance ASC
        """,
        nativeQuery = true)
    List<Object[]> findAvailableDriversNearbyWithDistance(
        @Param("lat") BigDecimal lat,
        @Param("lng") BigDecimal lng,
        @Param("radiusMeters") double radiusMeters
    );

    /**
     * Count available drivers within a specified radius.
     * Useful for analytics and job assignment optimization.
     */
    @Query(value = """
        SELECT COUNT(*)
        FROM drivers d
        WHERE d.is_available = true
          AND d.current_lat IS NOT NULL
          AND d.current_lng IS NOT NULL
          AND ST_DWithin(
              ST_MakePoint(d.current_lng, d.current_lat)::geography,
              ST_MakePoint(:lng, :lat)::geography,
              :radiusMeters
          )
        """,
        nativeQuery = true)
    long countAvailableDriversNearby(
        @Param("lat") BigDecimal lat,
        @Param("lng") BigDecimal lng,
        @Param("radiusMeters") double radiusMeters
    );
}
