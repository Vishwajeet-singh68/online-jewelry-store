package com.jewelry.cart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Cart item details response")
public class CartItemResponse {

    @Schema(description = "Cart item ID", example = "10")
    private Long id;

    @Schema(description = "Product ID", example = "101")
    private Long productId;

    @Schema(description = "Product SKU", example = "JW-RING-18K-001")
    private String sku;

    @Schema(description = "Product name", example = "Diamond Gold Ring")
    private String productName;

    @Schema(description = "Product image URL", example = "https://cdn.jewelry.com/ring.jpg")
    private String productImage;

    @Schema(description = "Unit price snapshot", example = "50000.00")
    private BigDecimal unitPrice;

    @Schema(description = "Quantity", example = "2")
    private Integer quantity;

    @Schema(description = "Item subtotal", example = "100000.00")
    private BigDecimal subtotal;
}
