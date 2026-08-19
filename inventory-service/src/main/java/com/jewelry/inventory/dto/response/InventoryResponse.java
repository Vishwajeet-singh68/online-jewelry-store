package com.jewelry.inventory.dto.response;

import com.jewelry.inventory.enums.InventoryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Inventory record response")
public class InventoryResponse {

    @Schema(description = "Internal inventory record ID")
    private Long id;

    @Schema(description = "Product ID from Product Service", example = "101")
    private Long productId;

    @Schema(description = "Stock Keeping Unit", example = "JW-RING-18K-001")
    private String sku;

    @Schema(description = "Stock available for purchase", example = "7")
    private Integer availableQuantity;

    @Schema(description = "Stock reserved by pending orders", example = "2")
    private Integer reservedQuantity;

    @Schema(description = "Total units permanently sold", example = "10")
    private Integer soldQuantity;

    @Schema(description = "Low stock alert threshold", example = "3")
    private Integer lowStockThreshold;

    @Schema(description = "Current inventory status", example = "ACTIVE")
    private InventoryStatus status;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Record last updated timestamp")
    private LocalDateTime updatedAt;
}
