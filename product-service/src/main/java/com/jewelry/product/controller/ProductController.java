package com.jewelry.product.controller;

import com.jewelry.product.dto.response.PageResponse;
import com.jewelry.product.dto.response.ProductResponse;
import com.jewelry.product.dto.response.ProductSummaryResponse;
import com.jewelry.product.entity.enums.Gender;
import com.jewelry.product.entity.enums.MetalType;
import com.jewelry.product.entity.enums.ProductStatus;
import com.jewelry.product.entity.enums.StoneType;
import com.jewelry.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Catalog", description = "Public endpoints for searching, filtering, and retrieving jewelry products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get active products", description = "Public endpoint with search, filtering, sorting, and pagination for ACTIVE products")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) MetalType metalType,
            @RequestParam(required = false) String purity,
            @RequestParam(required = false) StoneType stoneType,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        PageResponse<ProductSummaryResponse> response = productService.getProducts(
                categoryId, metalType, purity, stoneType, gender, minPrice, maxPrice,
                ProductStatus.ACTIVE, search, page, size, sort
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Public endpoint to retrieve complete product details")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU", description = "Public endpoint to lookup a product by unique SKU")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }
}
