package com.jewelry.inventory.controller;

import com.jewelry.inventory.dto.request.StockQuantityRequest;
import com.jewelry.inventory.dto.response.InventoryResponse;
import com.jewelry.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public/internal inventory operations.
 * Authentication and authorization are delegated to the API Gateway.
 *
 * GET  /api/v1/inventory/{productId}         — Public: check product availability
 * POST /api/v1/inventory/{productId}/reserve  — Internal: reserve stock (Order Service)
 * POST /api/v1/inventory/{productId}/release  — Internal: release reserved stock (order cancelled)
 * POST /api/v1/inventory/{productId}/deduct   — Internal: deduct reserved stock (order confirmed)
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Public and internal stock operations")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    @Operation(
        summary = "Get inventory by product ID",
        description = "Returns current stock information for a product."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventory found"),
        @ApiResponse(responseCode = "404", description = "Inventory not found for given product ID")
    })
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @PostMapping("/{productId}/reserve")
    @Operation(
        summary = "Reserve stock",
        description = "Reserves stock for a pending order. Moves quantity from available to reserved. " +
                      "Returns 409 CONFLICT if insufficient stock. Called by Order Service via OpenFeign."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock reserved successfully"),
        @ApiResponse(responseCode = "404", description = "Inventory not found"),
        @ApiResponse(responseCode = "409", description = "Insufficient stock available")
    })
    public ResponseEntity<InventoryResponse> reserveStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockQuantityRequest request) {
        return ResponseEntity.ok(inventoryService.reserveStock(productId, request));
    }

    @PostMapping("/{productId}/release")
    @Operation(
        summary = "Release reserved stock",
        description = "Releases previously reserved stock back to available. Called when an order is cancelled."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock released successfully"),
        @ApiResponse(responseCode = "404", description = "Inventory not found"),
        @ApiResponse(responseCode = "409", description = "Cannot release more than reserved")
    })
    public ResponseEntity<InventoryResponse> releaseStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockQuantityRequest request) {
        return ResponseEntity.ok(inventoryService.releaseStock(productId, request));
    }

    @PostMapping("/{productId}/deduct")
    @Operation(
        summary = "Deduct reserved stock",
        description = "Permanently deducts reserved stock after a successful order. " +
                      "Moves quantity from reserved to sold. Does NOT reduce available quantity."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock deducted successfully"),
        @ApiResponse(responseCode = "404", description = "Inventory not found"),
        @ApiResponse(responseCode = "409", description = "Cannot deduct more than reserved")
    })
    public ResponseEntity<InventoryResponse> deductStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockQuantityRequest request) {
        return ResponseEntity.ok(inventoryService.deductStock(productId, request));
    }
}
