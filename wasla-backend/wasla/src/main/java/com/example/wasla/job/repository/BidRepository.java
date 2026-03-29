package com.example.wasla.job.repository;

import com.example.wasla.job.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {

    List<Bid> findAllByJobIdOrderByPriceAsc(UUID jobId);

    Optional<Bid> findByJobIdAndDriverId(UUID jobId, UUID driverId);

    boolean existsByJobIdAndDriverId(UUID jobId, UUID driverId);

    @Modifying
    @Query("UPDATE Bid b SET b.status = 'WITHDRAWN' WHERE b.job.id = :jobId AND b.id <> :acceptedBidId AND b.status = 'PENDING'")
    int withdrawOtherBids(@Param("jobId") UUID jobId, @Param("acceptedBidId") UUID acceptedBidId);

    List<Bid> findAllByDriverIdOrderByCreatedAtDesc(UUID driverId);
}
