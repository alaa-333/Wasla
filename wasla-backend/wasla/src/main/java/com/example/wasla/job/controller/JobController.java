package com.example.wasla.job.controller;

import com.example.wasla.auth.security.SecurityHelper;
import com.example.wasla.common.dto.ApiResponse;
import com.example.wasla.job.dto.*;
import com.example.wasla.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Job creation and management")
public class JobController {

    private final JobService jobService;
    private final SecurityHelper securityHelper;

    @Operation(summary = "Create a new job (CLIENT only)")
    @PostMapping
    public ResponseEntity<ApiResponse<JobResponseDto>> createJob(
            @RequestBody @Valid CreateJobRequest request) {
        var client = securityHelper.getCurrentClient();
        var response = jobService.createJob(client, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Job created successfully", response));
    }

    @Operation(summary = "Get job by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponseDto>> getJobById(@PathVariable UUID id) {
        var response = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> cancelJobById(@PathVariable UUID id) {

        boolean isCanceled = jobService.cancelJobById(id);

        if (isCanceled) {
            return ResponseEntity.ok(ApiResponse.ok("job canceled successfully"));
        } else {
            return ResponseEntity.ok(ApiResponse.ok("job not canceled "));

        }
    }

    @Operation(summary = "Get my jobs (client or driver)")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<JobListDto>>> getMyJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JobListDto> response;
        
        if (securityHelper.isClient()) {
            var clientId = securityHelper.getCurrentClient().getId();
            response = jobService.getClientJobs(clientId, pageable);
        } else {
            var driverId = securityHelper.getCurrentDriver().getId();
            response = jobService.getDriverJobs(driverId, pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "Get open jobs nearby (DRIVER only)")
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<JobListDto>>> getNearbyJobs(
            @RequestParam(required = false) BigDecimal lat,
            @RequestParam(required = false) BigDecimal lng,
            @RequestParam(defaultValue = "15") BigDecimal radiusKm) {
        var response = jobService.getNearbyJobs(lat, lng, radiusKm);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "Update job status (DRIVER only)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<JobResponseDto>> updateJobStatus(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateJobStatusRequest request) {
        var driver = securityHelper.getCurrentDriver();
        var response = jobService.updateJobStatus(id, driver, request);
        return ResponseEntity.ok(ApiResponse.ok("Job status updated", response));
    }
}
