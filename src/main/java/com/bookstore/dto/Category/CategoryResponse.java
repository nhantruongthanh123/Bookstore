package com.bookstore.dto.Category;

import com.bookstore.dto.Book.BookResponse;

import java.util.List;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        List<BookResponse> books
) {}
