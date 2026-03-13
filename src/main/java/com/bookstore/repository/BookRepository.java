package com.bookstore.repository;


import com.bookstore.entity.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepository {
    List<Book> books;

    public BookRepository() {
        books = new ArrayList<>();
    }
}
