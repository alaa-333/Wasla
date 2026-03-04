package com.example.wasla.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {


    @Builder.Default
    private String tokenType = "Bearer";
    private String subject;
    private String accessToken;
    private String expirationIn;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}