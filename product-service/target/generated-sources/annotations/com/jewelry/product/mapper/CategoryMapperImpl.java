package com.jewelry.product.mapper;

import com.jewelry.product.dto.request.CreateCategoryRequest;
import com.jewelry.product.dto.response.CategoryResponse;
import com.jewelry.product.entity.Category;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T00:51:56+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 20.0.2 (Oracle Corporation)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public Category toEntity(CreateCategoryRequest request) {
        if ( request == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.name( request.getName() );
        category.description( request.getDescription() );

        return category.build();
    }

    @Override
    public CategoryResponse toResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryResponse.CategoryResponseBuilder categoryResponse = CategoryResponse.builder();

        categoryResponse.id( category.getId() );
        categoryResponse.name( category.getName() );
        categoryResponse.description( category.getDescription() );
        categoryResponse.status( category.getStatus() );
        categoryResponse.createdAt( category.getCreatedAt() );
        categoryResponse.updatedAt( category.getUpdatedAt() );

        return categoryResponse.build();
    }
}
