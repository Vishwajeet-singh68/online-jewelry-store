package com.jewelry.product.service;

import com.jewelry.product.dto.request.CreateCategoryRequest;
import com.jewelry.product.dto.request.UpdateCategoryRequest;
import com.jewelry.product.dto.response.CategoryResponse;
import com.jewelry.product.entity.enums.CategoryStatus;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CreateCategoryRequest request);
    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);
    CategoryResponse updateCategoryStatus(Long id, CategoryStatus status);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
}
