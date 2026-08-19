package com.jewelry.order.client.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemClientDto {
    private Long id;
    private Long productId;
    private String sku;
    private String productName;
    private String productImage;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
}
