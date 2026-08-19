package com.jewelry.product.dto.response;

import com.jewelry.product.entity.enums.Gender;
import com.jewelry.product.entity.enums.MetalType;
import com.jewelry.product.entity.enums.ProductStatus;
import com.jewelry.product.entity.enums.StoneType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal discountPercentage;
    private BigDecimal finalPrice;
    private MetalType metalType;
    private String purity;
    private StoneType stoneType;
    private BigDecimal weight;
    private Gender gender;
    private List<ProductImageResponse> images;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
