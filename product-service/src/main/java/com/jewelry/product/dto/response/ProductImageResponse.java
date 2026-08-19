package com.jewelry.product.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageResponse {
    private String imageUrl;
    private String altText;
    private boolean primaryImage;
}
