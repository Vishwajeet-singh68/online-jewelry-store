package com.jewelry.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Schema(description = "Request to create inventory for a new product")
public class CreateInventoryRequest {

    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be a positive number")
    @Schema(description = "ID of the product in Product Service", example = "101")
    private Long productId;

    @NotBlank(message = "SKU is required")
    @Schema(description = "Stock Keeping Unit identifier", example = "JW-RING-18K-001")
    private String sku;

    @NotNull(message = "Initial quantity is required")
    @Min(value = 0, message = "Initial quantity cannot be negative")
    @Schema(description = "Initial stock quantity", example = "10")
    private Integer quantity;

    @Min(value = 0, message = "Low stock threshold cannot be negative")
    @Schema(description = "Threshold at which the item is considered low stock", example = "3")
    private Integer lowStockThreshold;
}
