package com.jewelry.product.service.impl;

import com.jewelry.product.dto.request.CreateProductRequest;
import com.jewelry.product.dto.request.ProductImageRequest;
import com.jewelry.product.dto.request.UpdateProductRequest;
import com.jewelry.product.dto.response.PageResponse;
import com.jewelry.product.dto.response.ProductResponse;
import com.jewelry.product.dto.response.ProductSummaryResponse;
import com.jewelry.product.entity.Category;
import com.jewelry.product.entity.Product;
import com.jewelry.product.entity.ProductImage;
import com.jewelry.product.entity.enums.Gender;
import com.jewelry.product.entity.enums.MetalType;
import com.jewelry.product.entity.enums.ProductStatus;
import com.jewelry.product.entity.enums.StoneType;
import com.jewelry.product.exception.BadRequestException;
import com.jewelry.product.exception.CategoryNotFoundException;
import com.jewelry.product.exception.DuplicateProductException;
import com.jewelry.product.exception.ProductNotFoundException;
import com.jewelry.product.mapper.ProductMapper;
import com.jewelry.product.repository.CategoryRepository;
import com.jewelry.product.repository.ProductRepository;
import com.jewelry.product.service.ProductService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final int MAX_PAGE_SIZE = 50;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("price", "name", "createdAt", "sku");

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateProductException("Product with SKU '" + request.getSku() + "' already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setStatus(ProductStatus.ACTIVE);

        if (request.getImages() != null) {
            List<ProductImage> images = request.getImages().stream()
                    .map(productMapper::toImageEntity)
                    .collect(Collectors.toList());
            product.setImages(images);
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = getProductEntity(id);

        if (request.getSku() != null && !request.getSku().isBlank()) {
            if (productRepository.existsBySkuAndIdNot(request.getSku(), id)) {
                throw new DuplicateProductException("Product with SKU '" + request.getSku() + "' already exists");
            }
            product.setSku(request.getSku());
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getDiscountPercentage() != null) {
            product.setDiscountPercentage(request.getDiscountPercentage());
        }
        if (request.getMetalType() != null) {
            product.setMetalType(request.getMetalType());
        }
        if (request.getPurity() != null) {
            product.setPurity(request.getPurity());
        }
        if (request.getStoneType() != null) {
            product.setStoneType(request.getStoneType());
        }
        if (request.getWeight() != null) {
            product.setWeight(request.getWeight());
        }
        if (request.getGender() != null) {
            product.setGender(request.getGender());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        if (request.getImages() != null) {
            List<ProductImage> images = request.getImages().stream()
                    .map(productMapper::toImageEntity)
                    .collect(Collectors.toList());
            product.setImages(images);
        }

        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProductStatus(Long id, ProductStatus status) {
        Product product = getProductEntity(id);
        product.setStatus(status);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return productMapper.toResponse(getProductEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> getProducts(
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
    ) {
        int validatedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Sort validatedSort = parseSort(sort);

        Pageable pageable = PageRequest.of(Math.max(page, 0), validatedSize, validatedSort);

        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }
            if (metalType != null) {
                predicates.add(criteriaBuilder.equal(root.get("metalType"), metalType));
            }
            if (purity != null && !purity.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("purity"), purity));
            }
            if (stoneType != null) {
                predicates.add(criteriaBuilder.equal(root.get("stoneType"), stoneType));
            }
            if (gender != null) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), gender));
            }
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (search != null && !search.isBlank()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate nameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern);
                Predicate descLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern);
                Predicate skuLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("sku")), searchPattern);
                predicates.add(criteriaBuilder.or(nameLike, descLike, skuLike));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<ProductSummaryResponse> productPage = productRepository.findAll(spec, pageable)
                .map(productMapper::toSummaryResponse);

        return PageResponse.from(productPage);
    }

    private Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    private Sort parseSort(String sortStr) {
        if (sortStr == null || sortStr.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sortStr.split(",");
        String field = parts[0].trim();

        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            throw new BadRequestException("Invalid sort field: '" + field + "'. Allowed sort fields are: " + ALLOWED_SORT_FIELDS);
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.DESC;
        }

        return Sort.by(direction, field);
    }
}
