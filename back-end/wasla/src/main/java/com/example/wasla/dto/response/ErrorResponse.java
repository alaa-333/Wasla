package com.example.wasla.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO for validation errors.
 * Contains timestamp, HTTP status, error message, and list of field errors.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {


    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private  String errorCode;
    private  int httpStatus;
    private  String path;
    @Builder.Default
    private List<FieldError> errors = new ArrayList<>();

}
