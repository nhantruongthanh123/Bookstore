package com.bookstore.mapper;

import com.bookstore.dto.Category.CategoryRequest;
import com.bookstore.dto.Category.CategoryResponse;
import com.bookstore.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);

    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequest categoryRequest);

    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateCategoryFromRequest(CategoryRequest request, @MappingTarget Category category);
}
