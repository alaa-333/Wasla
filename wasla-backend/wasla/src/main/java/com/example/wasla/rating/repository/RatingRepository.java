package com.example.wasla.rating.repository;

import com.example.wasla.rating.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    boolean existsByJobId(UUID jobId);

    Optional<Rating> findByJobId(UUID jobId);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.driver.id = :driverId")
    Double getAverageScoreForDriver(@Param("driverId") UUID driverId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.driver.id = :driverId")
    Long countByDriverId(@Param("driverId") UUID driverId);
}
