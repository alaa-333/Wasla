package com.example.wasla.job.service;

import com.example.wasla.common.exception.ErrorCode;
import com.example.wasla.common.exception.WaslaAppException;
import com.example.wasla.job.dto.*;
import com.example.wasla.job.entity.*;
import com.example.wasla.job.mapper.JobMapper;
import com.example.wasla.job.repository.JobRepository;
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
        // TODO: When PostGIS is integrated, use ST_DWithin spatial query
        // For now, return all open jobs that haven't expired
        return jobRepository.findOpenJobs(LocalDateTime.now())
                .stream()
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
}
