package com.jewelry.order.controller;

import com.jewelry.order.dto.request.UpdateOrderStatusRequest;
import com.jewelry.order.dto.response.OrderResponse;
import com.jewelry.order.dto.response.PageResponse;
import com.jewelry.order.enums.OrderStatus;
import com.jewelry.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Admin Orders", description = "Admin Order Management and Status Transition APIs")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders (Admin)", description = "Retrieves paginated orders across all users with optional status filter.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    })
    public ResponseEntity<PageResponse<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false) OrderStatus status) {
        int pageLimit = Math.min(size, 100);
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageResponse<OrderResponse> response = orderService.getAllOrders(PageRequest.of(page, pageLimit, sort), status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details by ID (Admin)", description = "Retrieves order details for administrative viewing.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getOrderByIdAdmin(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderByIdAdmin(orderId));
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status (Admin)", description = "Updates order status following state transition rules.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> updateOrderStatusAdmin(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatusAdmin(orderId, request));
    }
}
