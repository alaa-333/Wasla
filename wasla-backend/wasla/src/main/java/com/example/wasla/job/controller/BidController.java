package com.example.wasla.job.controller;

import com.example.wasla.auth.security.SecurityHelper;
import com.example.wasla.common.dto.ApiResponse;
import com.example.wasla.job.dto.BidResponseDto;
import com.example.wasla.job.dto.JobResponseDto;
import com.example.wasla.job.dto.SubmitBidRequest;
import com.example.wasla.job.service.BidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs/{jobId}/bids")
@RequiredArgsConstructor
@Tag(name = "Bids", description = "Bid submission and management")
public class BidController {

    private final BidService bidService;
    private final SecurityHelper securityHelper;

    @Operation(summary = "Submit a bid (DRIVER only)")
    @PostMapping
    public ResponseEntity<ApiResponse<BidResponseDto>> submitBid(
            @PathVariable UUID jobId,
            @RequestBody @Valid SubmitBidRequest request) {
        var driver = securityHelper.getCurrentDriver();
        var response = bidService.submitBid(jobId, driver, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Bid submitted successfully", response));
    }

    @Operation(summary = "Get all bids for a job (CLIENT only)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BidResponseDto>>> getBidsForJob(
            @PathVariable UUID jobId) {
        var response = bidService.getBidsForJob(jobId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "Accept a bid (CLIENT only)")
    @PatchMapping("/{bidId}/accept")
    public ResponseEntity<ApiResponse<JobResponseDto>> acceptBid(
            @PathVariable UUID jobId,
            @PathVariable UUID bidId) {
        var client = securityHelper.getCurrentClient();
        var response = bidService.acceptBid(jobId, bidId, client);
        return ResponseEntity.ok(ApiResponse.ok("Bid accepted", response));
    }
}
