package com.jewelry.order.controller;

import com.jewelry.order.context.CurrentUserProvider;
import com.jewelry.order.dto.request.CreateOrderRequest;
import com.jewelry.order.dto.response.OrderResponse;
import com.jewelry.order.dto.response.PageResponse;
import com.jewelry.order.enums.OrderStatus;
import com.jewelry.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Customer Orders", description = "Customer Order Placement, History, and Cancellation APIs")
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @Operation(summary = "Place order from cart", description = "Validates user cart, reserves inventory, creates order, and clears cart. Accepts optional Idempotency-Key header.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Empty cart or validation failure"),
        @ApiResponse(responseCode = "409", description = "Stock reservation failure downstream"),
        @ApiResponse(responseCode = "503", description = "Downstream service unavailable")
    })
    public ResponseEntity<OrderResponse> createOrder(
            HttpServletRequest httpRequest,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request) {
        Long userId = currentUserProvider.getCurrentUserId(httpRequest);
        OrderResponse response = orderService.createOrder(userId, request, idempotencyKey);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get user order history", description = "Retrieves paginated orders for current user with optional status filter.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order list retrieved successfully")
    })
    public ResponseEntity<PageResponse<OrderResponse>> getUserOrders(
            HttpServletRequest httpRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false) OrderStatus status) {
        Long userId = currentUserProvider.getCurrentUserId(httpRequest);
        int pageLimit = Math.min(size, 100);
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageResponse<OrderResponse> response = orderService.getUserOrders(userId, PageRequest.of(page, pageLimit, sort), status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get user order by ID", description = "Retrieves detailed order information. Enforces ownership check against X-User-Id.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getUserOrderById(
            HttpServletRequest httpRequest,
            @PathVariable Long orderId) {
        Long userId = currentUserProvider.getCurrentUserId(httpRequest);
        return ResponseEntity.ok(orderService.getUserOrderById(userId, orderId));
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel user order", description = "Cancels pending/confirmed/processing order and releases reserved stock.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "409", description = "Order state does not permit cancellation")
    })
    public ResponseEntity<OrderResponse> cancelOrder(
            HttpServletRequest httpRequest,
            @PathVariable Long orderId) {
        Long userId = currentUserProvider.getCurrentUserId(httpRequest);
        return ResponseEntity.ok(orderService.cancelOrder(userId, orderId));
    }
}
