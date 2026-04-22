package com.example.wasla.job.service;

import com.example.wasla.common.exception.ErrorCode;
import com.example.wasla.common.exception.WaslaAppException;
import com.example.wasla.job.dto.BidResponseDto;
import com.example.wasla.job.dto.JobResponseDto;
import com.example.wasla.job.dto.SubmitBidRequest;
import com.example.wasla.job.entity.Bid;
import com.example.wasla.job.entity.BidStatus;
import com.example.wasla.job.entity.Job;
import com.example.wasla.job.entity.JobStatus;
import com.example.wasla.job.mapper.JobMapper;
import com.example.wasla.job.repository.BidRepository;
import com.example.wasla.job.repository.JobRepository;
import com.example.wasla.notification.service.NotificationPublisher;
import com.example.wasla.user.client.entity.Client;
import com.example.wasla.user.driver.entity.Driver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing bids on jobs.
 * Handles bid submission, retrieval, and acceptance.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final NotificationPublisher notificationPublisher;

    @Transactional
    public BidResponseDto submitBid(UUID jobId, Driver driver, SubmitBidRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.JOB_NOT_FOUND));

        // Validate job is still accepting bids
        if (job.getStatus() != JobStatus.OPEN && job.getStatus() != JobStatus.BIDDING) {
            throw new WaslaAppException(ErrorCode.JOB_NOT_OPEN);
        }
        if (job.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new WaslaAppException(ErrorCode.JOB_EXPIRED);
        }

        // Validate driver is available
        if (!driver.getIsAvailable()) {
            throw new WaslaAppException(ErrorCode.DRIVER_NOT_ONLINE);
        }

        // One bid per driver per job
        if (bidRepository.existsByJobIdAndDriverId(jobId, driver.getId())) {
            throw new WaslaAppException(ErrorCode.BID_ALREADY_PLACED);
        }

        Bid bid = jobMapper.toBid(request);
        bid.setJob(job);
        bid.setDriver(driver);
        bid.setStatus(BidStatus.PENDING);

        Bid saved = bidRepository.save(bid);

        // First bid transitions job to BIDDING
        if (job.getStatus() == JobStatus.OPEN) {
            job.setStatus(JobStatus.BIDDING);
            jobRepository.save(job);
        }

        log.info("Bid {} submitted on job {} by driver {}", saved.getId(), jobId, driver.getId());
        
        // Publish notification to client
        notificationPublisher.publishBidPlaced(
            jobId,
            saved.getId(),
            job.getClient().getId(),
            driver.getId(),
            driver.getFullName(),
            request.getPrice().toString()
        );
        
        return jobMapper.toBidResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<BidResponseDto> getBidsForJob(UUID jobId) {
        // Verify job exists
        jobRepository.findById(jobId)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.JOB_NOT_FOUND));

        return bidRepository.findAllByJobIdOrderByPriceAsc(jobId)
                .stream()
                .map(jobMapper::toBidResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BidResponseDto> getMyBids(UUID driverId) {
        return bidRepository.findAllByDriverIdOrderByCreatedAtDesc(driverId)
                .stream()
                .map(jobMapper::toBidResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public JobResponseDto acceptBid(UUID jobId, UUID bidId, Client client) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.JOB_NOT_FOUND));

        // Only job owner can accept
        if (!job.getClient().getId().equals(client.getId())) {
            throw new WaslaAppException(ErrorCode.ACCESS_DENIED);
        }
        if (job.getStatus() != JobStatus.BIDDING && job.getStatus() != JobStatus.OPEN) {
            throw new WaslaAppException(ErrorCode.JOB_NOT_OPEN);
        }

        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.BID_NOT_FOUND));
        
        // Verify bid belongs to this job
        if (!bid.getJob().getId().equals(jobId)) {
            throw new WaslaAppException(ErrorCode.BID_NOT_FOUND);
        }
        
        if (bid.getStatus() != BidStatus.PENDING) {
            throw new WaslaAppException(ErrorCode.BID_NOT_PENDING);
        }

        // Atomic bid acceptance
        bid.setStatus(BidStatus.ACCEPTED);
        bidRepository.save(bid);

        bidRepository.withdrawOtherBids(jobId, bidId);

        job.setStatus(JobStatus.CONFIRMED);
        job.setDriver(bid.getDriver());
        job.setAcceptedPrice(bid.getPrice());
        Job saved = jobRepository.save(job);

        log.info("Bid {} accepted on job {} — driver {} assigned, price {}",
                bidId, jobId, bid.getDriver().getId(), bid.getPrice());

        // Publish notification to winning driver
        notificationPublisher.publishBidAccepted(
            jobId,
            bidId,
            bid.getDriver().getId(),
            client.getId(),
            job.getCargoDesc() != null ? job.getCargoDesc() : "Your Job"
        );

        // Publish notifications to rejected drivers
        List<Bid> rejectedBids = bidRepository.findByJobIdAndStatus(jobId, BidStatus.WITHDRAWN);
        rejectedBids.forEach(rejectedBid -> {
            notificationPublisher.publishBidRejected(jobId, rejectedBid.getDriver().getId());
        });

        return jobMapper.toResponseDto(saved);
    }
}
