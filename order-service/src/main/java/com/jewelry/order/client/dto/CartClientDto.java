package com.jewelry.order.client.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartClientDto {
    private Long id;
    private Long userId;
    private List<CartItemClientDto> items;
    private BigDecimal totalAmount;
}
