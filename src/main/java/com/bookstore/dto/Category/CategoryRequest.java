package com.bookstore.dto.Category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "Category name is required")
        String name,

        String description
) {}
