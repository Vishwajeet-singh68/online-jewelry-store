package com.jewelry.cart.client.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryClientDto {
    private Long id;
    private Long productId;
    private String sku;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private String status; // ACTIVE, OUT_OF_STOCK, INACTIVE
}
