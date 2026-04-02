package com.bookstore.dto.Book;

import java.math.BigDecimal;

public record SearchBookRequest (
        String title,
        String author,
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {}
