package com.bookstore.service.book;

import com.bookstore.dto.Book.BookRequest;
import com.bookstore.dto.Book.BookResponse;
import com.bookstore.dto.Book.SearchBookRequest;
import com.bookstore.dto.Page.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    PageResponse<BookResponse> getAllBooks(Pageable pageable);
    BookResponse getBookById(Long id);
    BookResponse createBook(BookRequest bookRequest);
    BookResponse updateBook(Long id, BookRequest bookRequest);
    void deleteBook(Long id);
    PageResponse<BookResponse> searchBooks(SearchBookRequest searchBookRequest, Pageable pageable);
}
