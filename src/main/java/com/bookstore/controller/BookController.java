package com.bookstore.controller;

import com.bookstore.dto.Book.BookRequest;
import com.bookstore.dto.Book.BookResponse;

import com.bookstore.service.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookResponse> getAllBooks(){
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable Long id){
        return bookService.getBookById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponse createBook(@RequestBody BookRequest book){
        return bookService.createBook(book);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponse updateBook(@RequestBody BookRequest book, @PathVariable Long id){
        return  bookService.updateBook(id, book);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id){

        bookService.deleteBook(id);
    }
}
