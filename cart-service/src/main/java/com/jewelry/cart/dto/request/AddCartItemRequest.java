package com.jewelry.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to add product item to user's cart")
public class AddCartItemRequest {

    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    @Schema(description = "ID of the product from Product Service", example = "101")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "Quantity of product to add", example = "2")
    private Integer quantity;
}
