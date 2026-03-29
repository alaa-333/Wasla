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

    @Modifying
    @Query("UPDATE Job j SET j.status = 'EXPIRED' WHERE j.status IN ('OPEN', 'BIDDING') AND j.expiresAt < :now")
    int expireOldJobs(@Param("now") LocalDateTime now);
}
