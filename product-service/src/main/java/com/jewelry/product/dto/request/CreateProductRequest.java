package com.jewelry.product.dto.request;

import com.jewelry.product.entity.enums.Gender;
import com.jewelry.product.entity.enums.MetalType;
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
public class CreateProductRequest {

    @NotBlank(message = "SKU is required")
    @Size(min = 3, max = 100, message = "SKU must be between 3 and 100 characters")
    private String sku;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "Discount percentage cannot be negative")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100%")
    private BigDecimal discountPercentage;

    @NotNull(message = "Metal type is required")
    private MetalType metalType;

    private String purity;

    @NotNull(message = "Stone type is required")
    private StoneType stoneType;

    @DecimalMin(value = "0.01", message = "Weight must be greater than 0")
    private BigDecimal weight;

    private Gender gender;

    @Valid
    private List<ProductImageRequest> images;
}
