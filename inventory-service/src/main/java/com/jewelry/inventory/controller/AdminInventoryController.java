package com.jewelry.inventory.controller;

import com.jewelry.inventory.dto.request.CreateInventoryRequest;
import com.jewelry.inventory.dto.request.StockAdjustmentRequest;
import com.jewelry.inventory.dto.request.UpdateInventoryRequest;
import com.jewelry.inventory.dto.response.InventoryResponse;
import com.jewelry.inventory.dto.response.PageResponse;
import com.jewelry.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin inventory management controller.
 * Access control (admin-only) is enforced at the API Gateway level.
 */
@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
@Tag(name = "Admin - Inventory", description = "Admin inventory management operations")
public class AdminInventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @Operation(summary = "Create inventory", description = "Creates a new inventory record for a product. One record per product.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Inventory created"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "409", description = "Inventory already exists for product")
    })
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody CreateInventoryRequest request) {
        return new ResponseEntity<>(inventoryService.createInventory(request), HttpStatus.CREATED);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get inventory by product ID", description = "Full inventory details including reserved and sold quantities.")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @GetMapping
    @Operation(summary = "List all inventory", description = "Paginated list of all inventory records.")
    public ResponseEntity<PageResponse<InventoryResponse>> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(inventoryService.getAllInventory(page, size));
    }

    @PutMapping("/{productId}")
    @Operation(
        summary = "Update inventory settings",
        description = "Update SKU, low stock threshold, or status. Does NOT directly set quantity fields."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventory updated"),
        @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateInventoryRequest request) {
        return ResponseEntity.ok(inventoryService.updateInventory(productId, request));
    }

    @PatchMapping("/{productId}/adjust")
    @Operation(
        summary = "Adjust stock quantity",
        description = "Adjust available stock by a delta (positive or negative). " +
                      "Reasons: RESTOCK, DAMAGE, CORRECTION, RETURN, OTHER. " +
                      "Resulting quantity must not be negative."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock adjusted"),
        @ApiResponse(responseCode = "409", description = "Adjustment would result in negative quantity"),
        @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    public ResponseEntity<InventoryResponse> adjustStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.ok(inventoryService.adjustStock(productId, request));
    }

    @GetMapping("/low-stock")
    @Operation(
        summary = "Get low stock items",
        description = "Returns items where availableQuantity <= lowStockThreshold. Excludes INACTIVE items."
    )
    public ResponseEntity<PageResponse<InventoryResponse>> getLowStockInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(inventoryService.getLowStockInventory(page, size));
    }
}
