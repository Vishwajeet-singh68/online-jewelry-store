package com.jewelry.cart.client.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductClientDto {
    private Long id;
    private String name;
    private String sku;
    private String imageUrl;
    private BigDecimal price;
    private String status; // ACTIVE, INACTIVE, etc.
}
