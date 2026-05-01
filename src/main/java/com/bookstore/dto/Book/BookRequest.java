package com.bookstore.dto.Book;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Set;

public record BookRequest(
        @jakarta.validation.constraints.NotBlank(message = "Title is required")
        String title,

        @NotEmpty(message = "At least one author is required")
        Set<Long> authorsIds,

        String publisher,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        String isbn,

        String description,

        String coverImage,

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer quantity,

        @NotEmpty(message = "At least one category is required")
        Set<Long> categoryIds
) {}
