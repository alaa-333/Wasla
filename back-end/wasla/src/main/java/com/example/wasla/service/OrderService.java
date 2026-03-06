package com.example.wasla.service;

import com.example.wasla.dto.request.CreateOrderRequest;
import com.example.wasla.dto.request.PaginationRequest;
import com.example.wasla.dto.response.*;
import com.example.wasla.exception.BusinessException;
import com.example.wasla.exception.ErrorCode;
import com.example.wasla.mapper.OrderMapper;
import com.example.wasla.model.User;
import com.example.wasla.model.enums.OrderStatus;
import com.example.wasla.repository.OrderRepository;
import com.example.wasla.util.GoogleDistanceService;
import com.example.wasla.util.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final GoogleDistanceService distanceService;
    private final PricingService pricingService;

    @PreAuthorize("hasAuthority('CLIENT')")
    @Transactional
    public OrderResponse createOrder(User user, CreateOrderRequest request) {

        var order = orderMapper.toEntity(request);

        String origin = request.getPickupLat() + "," + request.getPickupLng();
        String destination = request.getDropLat() + "," + request.getDropLng();

        // calc distance via google map api
        var googleApiDistanceResponse = distanceService.getDistance(origin, destination);

        if (googleApiDistanceResponse == null || googleApiDistanceResponse.rows == null
                || googleApiDistanceResponse.rows.length == 0
                || googleApiDistanceResponse.rows[0].elements == null
                || googleApiDistanceResponse.rows[0].elements.length == 0) {
            throw new RuntimeException("Invalid response from Google Distance API");
        }

        // get distance and convert form m -> km
        var distance = (googleApiDistanceResponse.rows[0].elements[0].distance.value / 1000.0);

        // pricing algorithm calc price based on (distance, weight)
        var price = pricingService.calculatePrice(distance, request.getWeight());

        order.setClient(user);
        order.setPrice(price);
        order.setStatus(OrderStatus.PENDING);

        var savedOrder = orderRepository.save(order);

        var orderResponse = orderMapper.toOrderResponse(googleApiDistanceResponse);
        orderResponse.setStatus(savedOrder.getStatus().toString());
        orderResponse.setOrderId(savedOrder.getId());
        OrderResponse.PricingDTO pricingDTO = new OrderResponse.PricingDTO();
        pricingDTO.setCurrency("EGY");
        pricingDTO.setTotalPrice(price.doubleValue());
        orderResponse.setPricing(pricingDTO);

        return orderResponse;
    }



    @PreAuthorize("hasAuthority('CLIENT')")
    public PaginationResponse<OrderHistoryResponse> getAll(Long userId, PaginationRequest request) {

        var pageResponse = orderRepository.findAllByClientId(userId, request.toPageable())
                .map(orderMapper::toHistoryResponse);
        return PaginationResponse.of(pageResponse);
    }



    @PreAuthorize("hasAuthority('CLIENT')")
    public OrderDetailsResponse getById(Long userId, Long orderId) {

        var order = orderRepository.findByIdAndClientId(orderId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        return orderMapper.toDetailsResponse(order);

    }

}
