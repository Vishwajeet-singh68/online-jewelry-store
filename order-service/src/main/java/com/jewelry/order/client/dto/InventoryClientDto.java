package com.jewelry.order.client.dto;

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
    private String status;
}
