package com.jewelry.cart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Cart validation issue detail")
public class CartValidationIssue {

    @Schema(description = "Product ID affected", example = "101")
    private Long productId;

    @Schema(description = "Type of validation issue", example = "PRICE_CHANGED")
    private String type;

    @Schema(description = "Detailed issue message", example = "Product price has changed")
    private String message;
}
