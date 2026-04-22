package com.example.wasla.job.service;

import com.example.wasla.common.exception.ErrorCode;
import com.example.wasla.common.exception.WaslaAppException;
import com.example.wasla.job.dto.*;
import com.example.wasla.job.entity.*;
import com.example.wasla.job.mapper.JobMapper;
import com.example.wasla.job.repository.JobRepository;
import com.example.wasla.notification.service.NotificationPublisher;
import com.example.wasla.user.client.entity.Client;
import com.example.wasla.user.driver.entity.Driver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Core business logic for job management.
 * Handles job creation, retrieval, and status transitions.
 * Works with separate Client and Driver entities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final NotificationPublisher notificationPublisher;

    private static final int JOB_EXPIRY_MINUTES = 30;

    // ── Job Operations ──

    @Transactional
    public JobResponseDto createJob(Client client, CreateJobRequest request) {
        Job job = jobMapper.toJob(request);
        job.setClient(client);
        job.setStatus(JobStatus.OPEN);
        job.setExpiresAt(LocalDateTime.now().plusMinutes(JOB_EXPIRY_MINUTES));

        Job saved = jobRepository.save(job);
        log.info("Job created: id={} by client={}", saved.getId(), client.getId());
        
        // Publish notification event
        notificationPublisher.publishJobPosted(
            saved.getId(), 
            client.getId(), 
            saved.getCargoDesc() != null ? saved.getCargoDesc() : "New Job"
        );
        
        return jobMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public JobResponseDto getJobById(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.JOB_NOT_FOUND));
        return jobMapper.toResponseDto(job);
    }

    @Transactional(readOnly = true)
    public Page<JobListDto> getClientJobs(UUID clientId, Pageable pageable) {
        Page<Job> jobs = jobRepository.findAllByClientId(clientId, pageable);
        return jobs.map(jobMapper::toListDto);
    }

    @Transactional(readOnly = true)
    public Page<JobListDto> getDriverJobs(UUID driverId, Pageable pageable) {
        Page<Job> jobs = jobRepository.findAllByDriverId(driverId, pageable);
        return jobs.map(jobMapper::toListDto);
    }

    @Transactional(readOnly = true)
    public List<JobListDto> getNearbyJobs(BigDecimal lat, BigDecimal lng, BigDecimal radiusKm) {
        // Convert km to meters for PostGIS
        double radiusMeters = radiusKm.multiply(BigDecimal.valueOf(1000)).doubleValue();
        
        // Use PostGIS ST_DWithin spatial query for accurate distance-based filtering
        List<Job> nearbyJobs = jobRepository.findNearbyJobs(lat, lng, radiusMeters, LocalDateTime.now());
        
        log.info("Found {} nearby jobs within {} km of ({}, {})", nearbyJobs.size(), radiusKm, lat, lng);
        
        return nearbyJobs.stream()
                .map(jobMapper::toListDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobResponseDto updateJobStatus(UUID jobId, Driver driver, UpdateJobStatusRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.JOB_NOT_FOUND));

        // Only the assigned driver can update status
        if (job.getDriver() == null || !job.getDriver().getId().equals(driver.getId())) {
            throw new WaslaAppException(ErrorCode.ACCESS_DENIED);
        }

        JobStatus newStatus = JobStatus.valueOf(request.getStatus());
        validateStatusTransition(job.getStatus(), newStatus);

        job.setStatus(newStatus);
        if (newStatus == JobStatus.COMPLETED) {
            job.setCompletedAt(LocalDateTime.now());
        }

        Job saved = jobRepository.save(job);
        log.info("Job {} status updated: {} → {}", jobId, job.getStatus(), newStatus);
        
        // Publish notification events based on status
        if (newStatus == JobStatus.IN_PROGRESS) {
            notificationPublisher.publishJobStarted(
                jobId, 
                job.getClient().getId(), 
                driver.getId(), 
                driver.getFullName()
            );
        } else if (newStatus == JobStatus.COMPLETED) {
            notificationPublisher.publishJobCompleted(
                jobId, 
                job.getClient().getId(), 
                driver.getId(), 
                driver.getFullName()
            );
        }
        
        return jobMapper.toResponseDto(saved);
    }

    // ── Validation ──

    private void validateStatusTransition(JobStatus current, JobStatus next) {
        boolean valid = switch (current) {
            case CONFIRMED -> next == JobStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == JobStatus.COMPLETED;
            default -> false;
        };
        if (!valid) {
            throw new WaslaAppException(ErrorCode.JOB_STATUS_INVALID);
        }
    }

    public boolean cancelJobById(UUID id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.JOB_NOT_FOUND));


        if (job.getStatus() == JobStatus.CANCELLED) {
            throw new WaslaAppException(ErrorCode.JOB_STATUS_INVALID);
        }

        // Fix: Use AND instead of OR - job must be either OPEN or BIDDING to cancel
        if (job.getStatus() != JobStatus.OPEN && job.getStatus() != JobStatus.BIDDING) {
            throw new WaslaAppException(ErrorCode.JOB_STATUS_INVALID);
        }

        job.setStatus(JobStatus.CANCELLED);
        return true;

    }
}
