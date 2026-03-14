package com.bookstore.service.book;

import com.bookstore.entity.Book;

import java.util.List;

public interface BookService {
    List<Book> getAllBooks();
    Book getBookById(int id);
    Book createBook(Book book);
    Book updateBook(Integer id, Book bookDetails);
    void deleteBook(Integer id);
}
