package com.bookstore.dto.Category;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

public record CategoryResponse(
        Long id,
        String name,
        String description
) {}
