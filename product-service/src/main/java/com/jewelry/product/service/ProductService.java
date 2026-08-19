package com.jewelry.product.service;

import com.jewelry.product.dto.request.CreateProductRequest;
import com.jewelry.product.dto.request.UpdateProductRequest;
import com.jewelry.product.dto.response.PageResponse;
import com.jewelry.product.dto.response.ProductResponse;
import com.jewelry.product.dto.response.ProductSummaryResponse;
import com.jewelry.product.entity.enums.Gender;
import com.jewelry.product.entity.enums.MetalType;
import com.jewelry.product.entity.enums.ProductStatus;
import com.jewelry.product.entity.enums.StoneType;

import java.math.BigDecimal;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse updateProduct(Long id, UpdateProductRequest request);
    ProductResponse updateProductStatus(Long id, ProductStatus status);
    ProductResponse getProductById(Long id);
    ProductResponse getProductBySku(String sku);

    PageResponse<ProductSummaryResponse> getProducts(
            Long categoryId,
            MetalType metalType,
            String purity,
            StoneType stoneType,
            Gender gender,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            ProductStatus status,
            String search,
            int page,
            int size,
            String sort
    );
}
