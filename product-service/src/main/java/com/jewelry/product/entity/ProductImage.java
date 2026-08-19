package com.jewelry.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(length = 255)
    private String altText;

    @Column(nullable = false)
    private boolean primaryImage;
}
