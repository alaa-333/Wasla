package com.example.wasla.job.scheduler;

import com.example.wasla.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Scheduled task that expires jobs older than 30 minutes.
 * Runs every 60 seconds as documented in TECHNICAL_DOCUMENTATION.md Section 4.5
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobExpiryScheduler {

    private final JobRepository jobRepository;

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void expireOldJobs() {
        int expired = jobRepository.expireOldJobs(LocalDateTime.now());
        if (expired > 0) {
            log.info("Expired {} jobs that passed their deadline", expired);
        }
    }
}
