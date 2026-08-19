package com.jewelry.product.dto.response;

import com.jewelry.product.entity.enums.CategoryStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private CategoryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
