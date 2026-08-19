package com.jewelry.product.entity;

import com.jewelry.product.entity.enums.Gender;
import com.jewelry.product.entity.enums.MetalType;
import com.jewelry.product.entity.enums.ProductStatus;
import com.jewelry.product.entity.enums.StoneType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_sku", columnList = "sku"),
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_category_id", columnList = "category_id"),
        @Index(name = "idx_product_metal_type", columnList = "metalType"),
        @Index(name = "idx_product_stone_type", columnList = "stoneType"),
        @Index(name = "idx_product_price", columnList = "price"),
        @Index(name = "idx_product_status", columnList = "status"),
        @Index(name = "idx_product_created_at", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MetalType metalType;

    @Column(length = 50)
    private String purity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StoneType stoneType;

    @Column(precision = 8, scale = 3)
    private BigDecimal weight;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
