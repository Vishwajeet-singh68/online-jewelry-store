package com.jewelry.cart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Cart details response")
public class CartResponse {

    @Schema(description = "Cart ID", example = "1")
    private Long id;

    @Schema(description = "User ID derived from JWT", example = "101")
    private Long userId;

    @Schema(description = "List of items in cart")
    private List<CartItemResponse> items;

    @Schema(description = "Total cart amount", example = "100000.00")
    private BigDecimal totalAmount;

    @Schema(description = "Cart creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Cart last modified timestamp")
    private LocalDateTime updatedAt;
}
