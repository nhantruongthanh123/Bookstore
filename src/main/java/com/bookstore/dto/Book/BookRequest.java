package com.bookstore.dto.Book;

import java.util.Set;

public record BookRequest(
        String title,
        String author,
        String publisher,
        double price,
        String isbn,
        String description,
        String cover_image,
        Integer quantity,
        Set<Long> categoryIds
) {}
