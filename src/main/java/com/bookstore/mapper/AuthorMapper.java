package com.bookstore.mapper;

import com.bookstore.dto.Author.AuthorResponse;
import com.bookstore.entity.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorResponse toResponse(Author author);

}

