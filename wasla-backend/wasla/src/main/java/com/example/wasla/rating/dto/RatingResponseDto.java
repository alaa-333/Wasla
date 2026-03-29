package com.example.wasla.rating.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDto {
    private String id; // Changed from Long to String (UUID)
    private String jobId; // Changed from Long to String (UUID)
    private Short score; // Changed from Integer to Short
    private String comment;
    private LocalDateTime createdAt;
}
