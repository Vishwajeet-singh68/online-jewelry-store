package com.jewelry.product.service;

import com.jewelry.product.dto.request.CreateCategoryRequest;
import com.jewelry.product.dto.response.CategoryResponse;
import com.jewelry.product.entity.Category;
import com.jewelry.product.entity.enums.CategoryStatus;
import com.jewelry.product.exception.CategoryNotFoundException;
import com.jewelry.product.exception.DuplicateProductException;
import com.jewelry.product.mapper.CategoryMapper;
import com.jewelry.product.repository.CategoryRepository;
import com.jewelry.product.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CreateCategoryRequest createRequest;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("RINGS")
                .description("Rings collection")
                .status(CategoryStatus.ACTIVE)
                .build();

        createRequest = CreateCategoryRequest.builder()
                .name("Rings")
                .description("Rings collection")
                .build();
    }

    @Test
    void createCategory_Success() {
        when(categoryRepository.existsByName("RINGS")).thenReturn(false);
        when(categoryMapper.toEntity(any())).thenReturn(category);
        when(categoryRepository.save(any())).thenReturn(category);
        when(categoryMapper.toResponse(any())).thenReturn(CategoryResponse.builder().id(1L).name("RINGS").build());

        CategoryResponse response = categoryService.createCategory(createRequest);

        assertNotNull(response);
        assertEquals("RINGS", response.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_DuplicateName_ThrowsException() {
        when(categoryRepository.existsByName("RINGS")).thenReturn(true);

        assertThrows(DuplicateProductException.class, () -> categoryService.createCategory(createRequest));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getCategoryById_NotFound_ThrowsException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(99L));
    }
}
