package com.jewelry.product.dto.response;

import com.jewelry.product.entity.enums.MetalType;
import com.jewelry.product.entity.enums.ProductStatus;
import com.jewelry.product.entity.enums.StoneType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSummaryResponse {
    private Long id;
    private String sku;
    private String name;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal discountPercentage;
    private BigDecimal finalPrice;
    private MetalType metalType;
    private StoneType stoneType;
    private String primaryImageUrl;
    private ProductStatus status;
}
