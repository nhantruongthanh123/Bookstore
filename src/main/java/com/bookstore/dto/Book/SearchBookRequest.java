package com.bookstore.dto.Book;

import com.bookstore.entity.Author;

import java.math.BigDecimal;

public record SearchBookRequest (
        String title,
        Author author,
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {}
