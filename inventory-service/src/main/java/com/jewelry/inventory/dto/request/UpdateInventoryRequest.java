package com.jewelry.inventory.dto.request;

import com.jewelry.inventory.enums.InventoryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to update inventory settings (threshold, SKU, status)")
public class UpdateInventoryRequest {

    @Schema(description = "Updated SKU", example = "JW-RING-18K-001-V2")
    private String sku;

    @Min(value = 0, message = "Low stock threshold cannot be negative")
    @Schema(description = "New low stock threshold", example = "5")
    private Integer lowStockThreshold;

    @Schema(description = "Explicitly set inventory status (ACTIVE, INACTIVE, OUT_OF_STOCK)")
    private InventoryStatus status;
}
