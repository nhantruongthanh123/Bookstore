package com.bookstore.mapper;

import com.bookstore.dto.Author.AuthorRequest;
import com.bookstore.dto.Author.AuthorResponse;
import com.bookstore.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorResponse toResponse(Author author);

    @Mapping(target = "books", ignore = true)
    Author toEntity(AuthorRequest request);
}

