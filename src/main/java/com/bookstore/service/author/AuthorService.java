package com.bookstore.service.author;

import com.bookstore.dto.Author.AuthorRequest;
import com.bookstore.dto.Author.AuthorResponse;
import com.bookstore.dto.Page.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AuthorService {
    PageResponse<AuthorResponse> getAuthors(String find, Pageable pageable);
    AuthorResponse getAuthorById(Long id);
    AuthorResponse createAuthor(AuthorRequest request);
    AuthorResponse updateAuthor(Long id, AuthorRequest request);
    void deleteAuthor(Long id);
}

