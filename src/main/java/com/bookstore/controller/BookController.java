package com.bookstore.controller;

import com.bookstore.dto.Book.BookRequest;
import com.bookstore.dto.Book.BookResponse;

import com.bookstore.dto.Book.SearchBookRequest;
import com.bookstore.dto.Page.PageResponse;
import com.bookstore.entity.Author;
import com.bookstore.service.book.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> getAllBooks(@PageableDefault(size = 5) Pageable pageable){
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> createBook(@RequestBody @Valid BookRequest book){
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(book));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> updateBook(@RequestBody @Valid BookRequest book, @PathVariable Long id){
        return ResponseEntity.ok(bookService.updateBook(id, book));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<BookResponse>> searchBook(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 20, sort = "title") Pageable pageable
    ){
        SearchBookRequest request = new SearchBookRequest(title, toAuthorFilter(author), category, minPrice, maxPrice);

        PageResponse<BookResponse> books = bookService.searchBooks(request, pageable);
        return ResponseEntity.ok(books);
    }

    private Author toAuthorFilter(String authorName) {
        if (authorName == null || authorName.isBlank()) {
            return null;
        }
        Author author = new Author();
        author.setName(authorName.trim());
        return author;
    }
}
