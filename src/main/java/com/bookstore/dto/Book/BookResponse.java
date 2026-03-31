package com.bookstore.dto.Book;

import com.bookstore.dto.Category.CategoryResponse;

import java.math.BigDecimal;
import java.util.Set;

public record BookResponse(
        Long id,
        String title,
        String author,
        String publisher,
        BigDecimal price,
        String isbn,
        String description,
        String coverImage,
        Integer quantity,
        Set<CategoryResponse> categories
) {}
