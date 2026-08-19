package com.jewelry.product.mapper;

import com.jewelry.product.dto.request.CreateProductRequest;
import com.jewelry.product.dto.request.ProductImageRequest;
import com.jewelry.product.dto.response.ProductImageResponse;
import com.jewelry.product.dto.response.ProductResponse;
import com.jewelry.product.dto.response.ProductSummaryResponse;
import com.jewelry.product.entity.Product;
import com.jewelry.product.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(CreateProductRequest request);

    ProductImage toImageEntity(ProductImageRequest request);

    ProductImageResponse toImageResponse(ProductImage image);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "finalPrice", source = "product", qualifiedByName = "calculateFinalPrice")
    ProductResponse toResponse(Product product);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "finalPrice", source = "product", qualifiedByName = "calculateFinalPrice")
    @Mapping(target = "primaryImageUrl", source = "product", qualifiedByName = "extractPrimaryImageUrl")
    ProductSummaryResponse toSummaryResponse(Product product);

    @Named("calculateFinalPrice")
    default BigDecimal calculateFinalPrice(Product product) {
        if (product == null || product.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal price = product.getPrice();
        BigDecimal discount = product.getDiscountPercentage();
        if (discount == null || discount.compareTo(BigDecimal.ZERO) <= 0) {
            return price;
        }
        BigDecimal discountAmount = price.multiply(discount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return price.subtract(discountAmount);
    }

    @Named("extractPrimaryImageUrl")
    default String extractPrimaryImageUrl(Product product) {
        if (product == null || product.getImages() == null || product.getImages().isEmpty()) {
            return null;
        }
        return product.getImages().stream()
                .filter(ProductImage::isPrimaryImage)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElseGet(() -> product.getImages().get(0).getImageUrl());
    }
}
