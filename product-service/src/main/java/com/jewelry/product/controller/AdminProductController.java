package com.jewelry.product.controller;

import com.jewelry.product.dto.request.CreateProductRequest;
import com.jewelry.product.dto.request.UpdateProductRequest;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@Tag(name = "Admin Product Management", description = "Admin endpoints to create, update, and manage product status")
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create product", description = "Admin endpoint to create a new product")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Admin endpoint to update full product details")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update product status", description = "Admin endpoint for soft deletion or status update (ACTIVE, INACTIVE, ARCHIVED)")
    public ResponseEntity<ProductResponse> updateProductStatus(
            @PathVariable Long id,
            @RequestParam ProductStatus status) {
        return ResponseEntity.ok(productService.updateProductStatus(id, status));
    }

    @GetMapping
    @Operation(summary = "List all products (Admin)", description = "Admin endpoint to list products across all statuses with pagination & search")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> getAllProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) MetalType metalType,
            @RequestParam(required = false) String purity,
            @RequestParam(required = false) StoneType stoneType,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        PageResponse<ProductSummaryResponse> response = productService.getProducts(
                categoryId, metalType, purity, stoneType, gender, minPrice, maxPrice,
                status, search, page, size, sort
        );
        return ResponseEntity.ok(response);
    }
}
