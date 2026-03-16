package com.bookstore.service.book;

import com.bookstore.dto.Book.BookRequest;
import com.bookstore.dto.Book.BookResponse;

import java.util.List;

public interface BookService {
    List<BookResponse> getAllBooks();
    BookResponse getBookById(long id);
    BookResponse createBook(BookRequest request);
    BookResponse updateBook(Long id, BookRequest bookDetails);
    void deleteBook(Long id);
}
