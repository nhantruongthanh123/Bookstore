package com.bookstore.mapper;


import com.bookstore.dto.Book.BookRequest;
import com.bookstore.dto.Book.BookResponse;
import com.bookstore.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface BookMapper {
    BookResponse toResponse(Book book);
    Book toEntity(BookRequest bookRequest);
    void updateBookFromRequest(BookRequest request, @MappingTarget Book book);
}
