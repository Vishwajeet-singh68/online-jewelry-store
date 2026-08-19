package com.jewelry.product.mapper;

import com.jewelry.product.dto.request.CreateProductRequest;
import com.jewelry.product.dto.request.ProductImageRequest;
import com.jewelry.product.dto.response.ProductImageResponse;
import com.jewelry.product.dto.response.ProductResponse;
import com.jewelry.product.dto.response.ProductSummaryResponse;
import com.jewelry.product.entity.Category;
import com.jewelry.product.entity.Product;
import com.jewelry.product.entity.ProductImage;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T00:51:56+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 20.0.2 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toEntity(CreateProductRequest request) {
        if ( request == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.sku( request.getSku() );
        product.name( request.getName() );
        product.description( request.getDescription() );
        product.price( request.getPrice() );
        product.discountPercentage( request.getDiscountPercentage() );
        product.metalType( request.getMetalType() );
        product.purity( request.getPurity() );
        product.stoneType( request.getStoneType() );
        product.weight( request.getWeight() );
        product.gender( request.getGender() );
        product.images( productImageRequestListToProductImageList( request.getImages() ) );

        return product.build();
    }

    @Override
    public ProductImage toImageEntity(ProductImageRequest request) {
        if ( request == null ) {
            return null;
        }

        ProductImage.ProductImageBuilder productImage = ProductImage.builder();

        productImage.imageUrl( request.getImageUrl() );
        productImage.altText( request.getAltText() );
        productImage.primaryImage( request.isPrimaryImage() );

        return productImage.build();
    }

    @Override
    public ProductImageResponse toImageResponse(ProductImage image) {
        if ( image == null ) {
            return null;
        }

        ProductImageResponse.ProductImageResponseBuilder productImageResponse = ProductImageResponse.builder();

        productImageResponse.imageUrl( image.getImageUrl() );
        productImageResponse.altText( image.getAltText() );
        productImageResponse.primaryImage( image.isPrimaryImage() );

        return productImageResponse.build();
    }

    @Override
    public ProductResponse toResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductResponse.ProductResponseBuilder productResponse = ProductResponse.builder();

        productResponse.categoryId( productCategoryId( product ) );
        productResponse.categoryName( productCategoryName( product ) );
        productResponse.finalPrice( calculateFinalPrice( product ) );
        productResponse.id( product.getId() );
        productResponse.sku( product.getSku() );
        productResponse.name( product.getName() );
        productResponse.description( product.getDescription() );
        productResponse.price( product.getPrice() );
        productResponse.discountPercentage( product.getDiscountPercentage() );
        productResponse.metalType( product.getMetalType() );
        productResponse.purity( product.getPurity() );
        productResponse.stoneType( product.getStoneType() );
        productResponse.weight( product.getWeight() );
        productResponse.gender( product.getGender() );
        productResponse.images( productImageListToProductImageResponseList( product.getImages() ) );
        productResponse.status( product.getStatus() );
        productResponse.createdAt( product.getCreatedAt() );
        productResponse.updatedAt( product.getUpdatedAt() );

        return productResponse.build();
    }

    @Override
    public ProductSummaryResponse toSummaryResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductSummaryResponse.ProductSummaryResponseBuilder productSummaryResponse = ProductSummaryResponse.builder();

        productSummaryResponse.categoryId( productCategoryId( product ) );
        productSummaryResponse.categoryName( productCategoryName( product ) );
        productSummaryResponse.finalPrice( calculateFinalPrice( product ) );
        productSummaryResponse.primaryImageUrl( extractPrimaryImageUrl( product ) );
        productSummaryResponse.id( product.getId() );
        productSummaryResponse.sku( product.getSku() );
        productSummaryResponse.name( product.getName() );
        productSummaryResponse.price( product.getPrice() );
        productSummaryResponse.discountPercentage( product.getDiscountPercentage() );
        productSummaryResponse.metalType( product.getMetalType() );
        productSummaryResponse.stoneType( product.getStoneType() );
        productSummaryResponse.status( product.getStatus() );

        return productSummaryResponse.build();
    }

    protected List<ProductImage> productImageRequestListToProductImageList(List<ProductImageRequest> list) {
        if ( list == null ) {
            return null;
        }

        List<ProductImage> list1 = new ArrayList<ProductImage>( list.size() );
        for ( ProductImageRequest productImageRequest : list ) {
            list1.add( toImageEntity( productImageRequest ) );
        }

        return list1;
    }

    private Long productCategoryId(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String productCategoryName(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    protected List<ProductImageResponse> productImageListToProductImageResponseList(List<ProductImage> list) {
        if ( list == null ) {
            return null;
        }

        List<ProductImageResponse> list1 = new ArrayList<ProductImageResponse>( list.size() );
        for ( ProductImage productImage : list ) {
            list1.add( toImageResponse( productImage ) );
        }

        return list1;
    }
}
