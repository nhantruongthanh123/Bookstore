package com.bookstore.service.category;

import com.bookstore.dto.Category.CategoryRequest;
import com.bookstore.dto.Category.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
    CategoryResponse createCategory(CategoryRequest category);
    CategoryResponse updateCategory(Long id, CategoryRequest categoryDetails);
    void deleteCategory(Long id);
}
