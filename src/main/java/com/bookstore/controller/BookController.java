package com.bookstore.controller;

import com.bookstore.dto.Book.BookRequest;
import com.bookstore.dto.Book.BookResponse;

import com.bookstore.service.book.BookService;
import lombok.RequiredArgsConstructor;
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
    public BookResponse createBook(@RequestBody BookRequest book){
        return bookService.createBook(book);
    }

    @PutMapping("/{id}")
    public BookResponse updateBook(@RequestBody BookRequest book, @PathVariable Long id){
        return  bookService.updateBook(id, book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id){

        bookService.deleteBook(id);
    }
}
