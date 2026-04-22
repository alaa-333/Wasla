package com.example.wasla.rating;

import com.example.wasla.common.exception.ErrorCode;
import com.example.wasla.common.exception.WaslaAppException;
import com.example.wasla.job.entity.Job;
import com.example.wasla.job.entity.JobStatus;
import com.example.wasla.job.repository.JobRepository;
import com.example.wasla.notification.service.NotificationPublisher;
import com.example.wasla.rating.dto.CreateRatingRequest;
import com.example.wasla.rating.dto.RatingResponseDto;
import com.example.wasla.rating.entity.Rating;
import com.example.wasla.rating.mapper.RatingMapper;
import com.example.wasla.rating.repository.RatingRepository;
import com.example.wasla.user.client.entity.Client;
import com.example.wasla.user.driver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Rating service — handles rating creation and driver rating updates.
 * Works with separate Client and Driver entities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final JobRepository jobRepository;
    private final DriverRepository driverRepository;
    private final RatingMapper ratingMapper;
    private final NotificationPublisher notificationPublisher;

    @Transactional
    public RatingResponseDto createRating(UUID jobId, Client client, CreateRatingRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.JOB_NOT_FOUND));

        // Validate
        if (job.getStatus() != JobStatus.COMPLETED) {
            throw new WaslaAppException(ErrorCode.RATING_JOB_NOT_COMPLETED);
        }
        if (!job.getClient().getId().equals(client.getId())) {
            throw new WaslaAppException(ErrorCode.RATING_NOT_JOB_OWNER);
        }
        if (ratingRepository.existsByJobId(jobId)) {
            throw new WaslaAppException(ErrorCode.RATING_ALREADY_EXISTS);
        }

        Rating rating = ratingMapper.toRating(request);
        rating.setJob(job);
        rating.setClient(client);
        rating.setDriver(job.getDriver());

        Rating saved = ratingRepository.save(rating);

        // Update driver's average rating
        updateDriverRating(job.getDriver().getId());

        log.info("Rating {} created for job {} by client {}", saved.getId(), jobId, client.getId());
        
        // Publish notification to driver
        notificationPublisher.publishReviewReceived(
            jobId,
            job.getDriver().getId(),
            client.getId(),
            request.getScore(),
            request.getComment()
        );
        
        return ratingMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public RatingResponseDto getRatingByJobId(UUID jobId) {
        Rating rating = ratingRepository.findByJobId(jobId)
                .orElseThrow(() -> new WaslaAppException(ErrorCode.JOB_NOT_FOUND));
        return ratingMapper.toResponseDto(rating);
    }

    private void updateDriverRating(UUID driverId) {
        Double avgDouble = ratingRepository.getAverageScoreForDriver(driverId);
        Long count = ratingRepository.countByDriverId(driverId);

        driverRepository.findById(driverId).ifPresent(driver -> {
            // Convert Double to BigDecimal with proper scale
            BigDecimal avg = avgDouble != null 
                ? BigDecimal.valueOf(avgDouble).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            
            driver.setRatingAvg(avg);
            driver.setTotalJobs(count != null ? count.intValue() : 0);
            driverRepository.save(driver);
            
            log.debug("Updated driver {} rating: avg={}, total={}", driverId, avg, count);
        });
    }
}
