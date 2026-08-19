package com.jewelry.cart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Cart pre-checkout validation response")
public class CartValidationResponse {

    @Schema(description = "Indicates whether cart is completely valid for checkout", example = "false")
    private boolean valid;

    @Schema(description = "List of issues found during validation")
    private List<CartValidationIssue> issues;
}
