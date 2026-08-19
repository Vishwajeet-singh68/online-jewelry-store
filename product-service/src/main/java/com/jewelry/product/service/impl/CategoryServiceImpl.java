package com.jewelry.product.service.impl;

import com.jewelry.product.dto.request.CreateCategoryRequest;
import com.jewelry.product.dto.request.UpdateCategoryRequest;
import com.jewelry.product.dto.response.CategoryResponse;
import com.jewelry.product.entity.Category;
import com.jewelry.product.entity.enums.CategoryStatus;
import com.jewelry.product.exception.CategoryNotFoundException;
import com.jewelry.product.exception.DuplicateProductException;
import com.jewelry.product.mapper.CategoryMapper;
import com.jewelry.product.repository.CategoryRepository;
import com.jewelry.product.service.CategoryService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @PostConstruct
    public void seedInitialCategories() {
        if (categoryRepository.count() == 0) {
            List<String> defaultCategories = Arrays.asList(
                    "RINGS", "NECKLACES", "EARRINGS", "BRACELETS",
                    "BANGLES", "PENDANTS", "CHAINS", "WEDDING_JEWELRY"
            );

            List<Category> categories = defaultCategories.stream()
                    .map(name -> Category.builder()
                            .name(name)
                            .description(name.replace("_", " ") + " collection")
                            .status(CategoryStatus.ACTIVE)
                            .build())
                    .collect(Collectors.toList());

            categoryRepository.saveAll(categories);
        }
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.getName().toUpperCase())) {
            throw new DuplicateProductException("Category with name '" + request.getName() + "' already exists");
        }

        Category category = categoryMapper.toEntity(request);
        category.setName(request.getName().toUpperCase());
        category.setStatus(CategoryStatus.ACTIVE);

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = getCategoryEntity(id);

        if (request.getName() != null && !request.getName().isBlank()) {
            String newName = request.getName().toUpperCase();
            if (categoryRepository.existsByNameAndIdNot(newName, id)) {
                throw new DuplicateProductException("Category with name '" + newName + "' already exists");
            }
            category.setName(newName);
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategoryStatus(Long id, CategoryStatus status) {
        Category category = getCategoryEntity(id);
        category.setStatus(status);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        return categoryMapper.toResponse(getCategoryEntity(id));
    }

    private Category getCategoryEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }
}
