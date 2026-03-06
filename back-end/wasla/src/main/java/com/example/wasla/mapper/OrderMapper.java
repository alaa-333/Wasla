package com.example.wasla.mapper;

import com.example.wasla.dto.request.CreateOrderRequest;
import com.example.wasla.dto.response.DistanceResponse;
import com.example.wasla.dto.response.OrderDetailsResponse;
import com.example.wasla.dto.response.OrderHistoryResponse;
import com.example.wasla.dto.response.OrderResponse;
import com.example.wasla.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // =============== TO ENTITY ====================
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "driver", ignore = true)
    Order toEntity(CreateOrderRequest request);




    // =============== TO OrderDetailsResponse ====================
    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "driverName", source = "driver.fullName")
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientName", source = "client.fullName")
    OrderDetailsResponse toDetailsResponse(Order order);




    // =============== TO toOrderResponse ====================
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "status", source = "status") // Maps the top-level status
    @Mapping(target = "pricing", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    // Mapping nested RoutingDetailsDTO
    @Mapping(target = "routingDetails", source = "source")
    OrderResponse toOrderResponse(DistanceResponse source);

    @Mapping(target = "originAddress", source = "origin_addresses", qualifiedByName = "getFirstString")
    @Mapping(target = "destinationAddress", source = "destination_addresses", qualifiedByName = "getFirstString")
    @Mapping(target = "distance", expression = "java(extractDistanceText(source))")
    @Mapping(target = "duration", expression = "java(extractDurationText(source))")
    OrderResponse.RoutingDetailsDTO toRoutingDetailsDTO(DistanceResponse source);

    @Named("getFirstString")
    default String getFirstString(String[] addresses) {
        return (addresses != null && addresses.length > 0) ? addresses[0] : null;
    }

    // Helper to safely dive into rows[0].elements[0].distance.text
    default String extractDistanceText(DistanceResponse source) {
        try {
            return source.getRows()[0].elements[0].distance.text;
        } catch (Exception e) {
            return null;
        }
    }

    // Helper to safely dive into rows[0].elements[0].duration.text
    default String extractDurationText(DistanceResponse source) {
        try {
            return source.getRows()[0].elements[0].duration.text;
        } catch (Exception e) {
            return null;
        }
    }




    // ============== toHistoryResponse ================
    OrderHistoryResponse toHistoryResponse(Order order);


}
