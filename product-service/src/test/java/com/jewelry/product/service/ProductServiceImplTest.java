package com.jewelry.product.service;

import com.jewelry.product.dto.request.CreateProductRequest;
import com.jewelry.product.dto.response.ProductResponse;
import com.jewelry.product.entity.Category;
import com.jewelry.product.entity.Product;
import com.jewelry.product.entity.enums.Gender;
import com.jewelry.product.entity.enums.MetalType;
import com.jewelry.product.entity.enums.ProductStatus;
import com.jewelry.product.entity.enums.StoneType;
import com.jewelry.product.exception.DuplicateProductException;
import com.jewelry.product.exception.ProductNotFoundException;
import com.jewelry.product.mapper.ProductMapper;
import com.jewelry.product.repository.CategoryRepository;
import com.jewelry.product.repository.ProductRepository;
import com.jewelry.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Category category;
    private CreateProductRequest createRequest;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("RINGS").build();
        product = Product.builder()
                .id(100L)
                .sku("JW-RING-001")
                .name("Diamond Ring")
                .price(BigDecimal.valueOf(50000))
                .discountPercentage(BigDecimal.valueOf(10))
                .category(category)
                .metalType(MetalType.GOLD)
                .stoneType(StoneType.DIAMOND)
                .status(ProductStatus.ACTIVE)
                .build();

        createRequest = CreateProductRequest.builder()
                .sku("JW-RING-001")
                .name("Diamond Ring")
                .categoryId(1L)
                .price(BigDecimal.valueOf(50000))
                .discountPercentage(BigDecimal.valueOf(10))
                .metalType(MetalType.GOLD)
                .stoneType(StoneType.DIAMOND)
                .build();
    }

    @Test
    void createProduct_Success() {
        when(productRepository.existsBySku("JW-RING-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productMapper.toEntity(any())).thenReturn(product);
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toResponse(any())).thenReturn(ProductResponse.builder()
                .id(100L)
                .sku("JW-RING-001")
                .price(BigDecimal.valueOf(50000))
                .finalPrice(BigDecimal.valueOf(45000))
                .build());

        ProductResponse response = productService.createProduct(createRequest);

        assertNotNull(response);
        assertEquals("JW-RING-001", response.getSku());
        assertEquals(BigDecimal.valueOf(45000), response.getFinalPrice());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_DuplicateSku_ThrowsException() {
        when(productRepository.existsBySku("JW-RING-001")).thenReturn(true);

        assertThrows(DuplicateProductException.class, () -> productService.createProduct(createRequest));
        verify(productRepository, never()).save(any());
    }

    @Test
    void getProductById_NotFound_ThrowsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(999L));
    }
}
