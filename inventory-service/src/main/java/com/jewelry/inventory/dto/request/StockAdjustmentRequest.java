package com.jewelry.inventory.dto.request;

import com.jewelry.inventory.enums.StockAdjustmentReason;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request to adjust stock quantity (positive = add, negative = remove)")
public class StockAdjustmentRequest {

    @NotNull(message = "Adjustment quantity is required")
    @Schema(
        description = "Amount to adjust. Positive adds stock (RESTOCK/RETURN). Negative removes stock (DAMAGE/CORRECTION).",
        example = "10"
    )
    private Integer quantity;

    @NotNull(message = "Adjustment reason is required")
    @Schema(description = "Reason for the adjustment", example = "RESTOCK")
    private StockAdjustmentReason reason;

    @Schema(description = "Optional note explaining the adjustment", example = "Received new shipment from supplier")
    private String note;
}
