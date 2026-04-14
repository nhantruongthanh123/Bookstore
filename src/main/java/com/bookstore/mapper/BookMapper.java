package com.bookstore.mapper;


import com.bookstore.dto.Book.BookRequest;
import com.bookstore.dto.Book.BookResponse;
import com.bookstore.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, AuthorMapper.class})
public interface BookMapper {
    @Mapping(target = "authors", source = "authors")
    BookResponse toResponse(Book book);

    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Book toEntity(BookRequest bookRequest);

    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    void updateBookFromRequest(BookRequest request, @MappingTarget Book book);
}
