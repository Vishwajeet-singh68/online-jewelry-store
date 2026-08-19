package com.jewelry.product.dto.request;

import com.jewelry.product.entity.enums.Gender;
import com.jewelry.product.entity.enums.MetalType;
import com.jewelry.product.entity.enums.ProductStatus;
import com.jewelry.product.entity.enums.StoneType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {

    @Size(min = 3, max = 100, message = "SKU must be between 3 and 100 characters")
    private String sku;

    @Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private Long categoryId;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "Discount percentage cannot be negative")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100%")
    private BigDecimal discountPercentage;

    private MetalType metalType;

    private String purity;

    private StoneType stoneType;

    @DecimalMin(value = "0.01", message = "Weight must be greater than 0")
    private BigDecimal weight;

    private Gender gender;

    private ProductStatus status;

    @Valid
    private List<ProductImageRequest> images;
}
