package com.example.wasla.rating;

import com.example.wasla.auth.security.SecurityHelper;
import com.example.wasla.common.dto.ApiResponse;
import com.example.wasla.rating.dto.CreateRatingRequest;
import com.example.wasla.rating.dto.RatingResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs/{jobId}/rating")
@RequiredArgsConstructor
@Tag(name = "Ratings", description = "Client-to-driver rating after job completion")
public class RatingController {

    private final RatingService ratingService;
    private final SecurityHelper securityHelper;

    @Operation(summary = "Rate a completed job (CLIENT only)")
    @PostMapping
    public ResponseEntity<ApiResponse<RatingResponseDto>> createRating(
            @PathVariable UUID jobId,
            @RequestBody @Valid CreateRatingRequest request) {
        var client = securityHelper.getCurrentClient();
        var response = ratingService.createRating(jobId, client, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Rating submitted", response));
    }

    @Operation(summary = "Get rating for a job")
    @GetMapping
    public ResponseEntity<ApiResponse<RatingResponseDto>> getRating(@PathVariable UUID jobId) {
        var response = ratingService.getRatingByJobId(jobId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
