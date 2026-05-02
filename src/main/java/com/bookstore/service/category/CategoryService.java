package com.bookstore.service.category;

import com.bookstore.dto.Category.CategoryRequest;
import com.bookstore.dto.Category.CategoryResponse;
import com.bookstore.dto.Page.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    PageResponse<CategoryResponse> getAllCategories(Pageable pageable);
    CategoryResponse getCategoryById(Long id);
    CategoryResponse createCategory(CategoryRequest category);
    CategoryResponse updateCategory(Long id, CategoryRequest categoryDetails);
    void deleteCategory(Long id);
}
