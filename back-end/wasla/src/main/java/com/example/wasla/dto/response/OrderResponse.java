package com.example.wasla.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private String status;
    private RoutingDetailsDTO routingDetails;
    private PricingDTO pricing;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Data
    public static class RoutingDetailsDTO {
        private String originAddress;
        private String destinationAddress;
        private String distance; // "12.4 km"
        private String duration; // "22 mins"
    }

    @Data
    public static class PricingDTO {
        private double totalPrice;
        private String currency;
    }
}