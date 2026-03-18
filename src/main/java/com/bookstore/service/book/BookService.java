package com.bookstore.service.book;

import com.bookstore.dto.Book.BookRequest;
import com.bookstore.dto.Book.BookResponse;

import java.util.List;

public interface BookService {
    List<BookResponse> getAllBooks();
    BookResponse getBookById(Long id);
    BookResponse createBook(BookRequest bookRequest);
    BookResponse updateBook(Long id, BookRequest bookRequest);
    void deleteBook(Long id);
}
