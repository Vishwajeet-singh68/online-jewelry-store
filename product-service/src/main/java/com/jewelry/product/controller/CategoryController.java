package com.jewelry.product.controller;

import com.jewelry.product.dto.request.CreateCategoryRequest;
import com.jewelry.product.dto.request.UpdateCategoryRequest;
import com.jewelry.product.dto.response.CategoryResponse;
import com.jewelry.product.entity.enums.CategoryStatus;
import com.jewelry.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "Public and Admin endpoints for jewelry categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Public endpoint to list all available jewelry categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/categories/{id}")
    @Operation(summary = "Get category by ID", description = "Public endpoint to retrieve category details")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping("/admin/categories")
    @Operation(summary = "Create category", description = "Admin endpoint to create a new jewelry category")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/admin/categories/{id}")
    @Operation(summary = "Update category", description = "Admin endpoint to update category details")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @PatchMapping("/admin/categories/{id}/status")
    @Operation(summary = "Update category status", description = "Admin endpoint to change category status (ACTIVE, INACTIVE, ARCHIVED)")
    public ResponseEntity<CategoryResponse> updateCategoryStatus(
            @PathVariable Long id,
            @RequestParam CategoryStatus status) {
        return ResponseEntity.ok(categoryService.updateCategoryStatus(id, status));
    }
}
