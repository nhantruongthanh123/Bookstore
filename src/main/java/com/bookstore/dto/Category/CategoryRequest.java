package com.bookstore.dto.Category;


import java.util.List;

public record CategoryRequest(
        String name,
        String description,
        List<Long> bookIds
) {}
