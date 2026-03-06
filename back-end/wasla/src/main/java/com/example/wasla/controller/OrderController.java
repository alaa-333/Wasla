package com.example.wasla.controller;

import com.example.wasla.dto.request.CreateOrderRequest;
import com.example.wasla.dto.request.PaginationRequest;
import com.example.wasla.dto.response.OrderHistoryResponse;
import com.example.wasla.dto.response.OrderDetailsResponse;
import com.example.wasla.dto.response.OrderResponse;
import com.example.wasla.dto.response.PaginationResponse;
import com.example.wasla.model.User;
import com.example.wasla.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;



    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody @Valid CreateOrderRequest request,
            @AuthenticationPrincipal User currentUser
            ) {

        var response = orderService.createOrder(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }






    @GetMapping("/history")
    public ResponseEntity<PaginationResponse<OrderHistoryResponse>> getAllOrders(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction

            ) {


        var paginationRequest = PaginationRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .build();

        var response = orderService.getAll(currentUser.getId(), paginationRequest); // TODO: pagination req
        return ResponseEntity.ok(response);
    }



    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsResponse> getById(
            @PathVariable(name = "id") @Positive @NotNull Long orderId,
            @AuthenticationPrincipal User currentUser
    ) {
        var response = orderService.getById(currentUser.getId(), orderId);
        return ResponseEntity.ok(response);
    }



    // TODO: @PutMapping -> update order status


}
