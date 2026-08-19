package com.jewelry.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageRequest {

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String altText;

    private boolean primaryImage;
}
