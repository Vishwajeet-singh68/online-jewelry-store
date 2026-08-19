package com.jewelry.product.dto.request;

import com.jewelry.product.entity.enums.CategoryStatus;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCategoryRequest {

    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private CategoryStatus status;
}
