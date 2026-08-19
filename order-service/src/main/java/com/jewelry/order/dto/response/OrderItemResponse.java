package com.jewelry.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Order item details response payload")
public class OrderItemResponse {

    @Schema(description = "Order item ID", example = "50")
    private Long id;

    @Schema(description = "Product ID", example = "101")
    private Long productId;

    @Schema(description = "Product SKU", example = "JW-RING-18K-001")
    private String sku;

    @Schema(description = "Purchased product snapshot name", example = "Diamond Gold Ring")
    private String productName;

    @Schema(description = "Product image URL", example = "https://cdn.jewelry.com/ring.jpg")
    private String productImage;

    @Schema(description = "Purchased unit price snapshot", example = "50000.00")
    private BigDecimal unitPrice;

    @Schema(description = "Purchased quantity", example = "2")
    private Integer quantity;

    @Schema(description = "Line item subtotal", example = "100000.00")
    private BigDecimal subtotal;
}
