package com.jewelry.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used for reserve, release, and deduct operations.
 * Quantity must always be positive — direction is implied by the endpoint.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request carrying a positive stock quantity for reserve/release/deduct")
public class StockQuantityRequest {

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    @Schema(description = "Number of units to reserve, release, or deduct", example = "2")
    private Integer quantity;
}
