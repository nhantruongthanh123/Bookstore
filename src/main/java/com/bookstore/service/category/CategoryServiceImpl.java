package com.bookstore.service.category;


import com.bookstore.dto.Category.CategoryRequest;
import com.bookstore.dto.Category.CategoryResponse;
import com.bookstore.dto.Page.PageResponse;
import com.bookstore.entity.Category;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Cacheable(value = "categories", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public PageResponse<CategoryResponse> getAllCategories(Pageable pageable){
        Page<CategoryResponse> categories = categoryRepository.findAll(pageable)
                .map(categoryMapper::toResponse);
        return PageResponse.of(categories);
    }

    @Override
    @Cacheable(value = "categories", key = "#id")
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No category with id: " + id));
        return categoryMapper.toResponse(category);
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CategoryRequest categoryRequest){
        Category newCategory = categoryMapper.toEntity(categoryRequest);

        Category savedCategory = categoryRepository.save(newCategory);

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No category with id: " + id));

        categoryMapper.updateCategoryFromRequest(categoryRequest, existingCategory);

        Category updatedCategory = categoryRepository.save(existingCategory);

        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("No category with id: " + id);
        }

        categoryRepository.deleteById(id);
    }

}
