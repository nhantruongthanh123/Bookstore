package com.bookstore.dto.Author;

public record AuthorResponse(
        Long id,
        String name,
        String description
) {
}

