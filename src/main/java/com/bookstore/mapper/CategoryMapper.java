package com.bookstore.mapper;

import com.bookstore.dto.Category.CategoryRequest;
import com.bookstore.dto.Category.CategoryResponse;
import com.bookstore.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toDto(Category category);
    Category toEntity(CategoryRequest categoryRequest);
    void updateCategoryFromRequest(CategoryRequest request, @MappingTarget Category category);
}
