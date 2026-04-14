package com.bookstore.controller;

import com.bookstore.dto.Author.AuthorRequest;
import com.bookstore.dto.Author.AuthorResponse;
import com.bookstore.dto.Page.PageResponse;
import com.bookstore.service.author.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping("/api/authors")
    public ResponseEntity<PageResponse<AuthorResponse>> getAuthors(
            @RequestParam(required = false) String find,
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        return ResponseEntity.ok(authorService.getAuthors(find, pageable));
    }

    @GetMapping("/api/authors/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @PostMapping("/api/admin/authors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponse> createAuthor(@RequestBody @Valid AuthorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(request));
    }

    @PatchMapping("/api/admin/authors/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable Long id,
            @RequestBody @Valid AuthorRequest request
    ) {
        return ResponseEntity.ok(authorService.updateAuthor(id, request));
    }

    @DeleteMapping("/api/admin/authors/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}

