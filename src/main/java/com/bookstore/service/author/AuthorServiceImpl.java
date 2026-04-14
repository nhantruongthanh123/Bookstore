package com.bookstore.service.author;

import com.bookstore.dto.Author.AuthorRequest;
import com.bookstore.dto.Author.AuthorResponse;
import com.bookstore.dto.Page.PageResponse;
import com.bookstore.entity.Author;
import com.bookstore.exception.DuplicateResourceException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.AuthorMapper;
import com.bookstore.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "description");

    @Override
    public PageResponse<AuthorResponse> getAuthors(String find, Pageable pageable) {
        Pageable safePageable = sanitizePageable(pageable);
        Page<Author> authors = (find == null || find.isBlank())
                ? authorRepository.findAll(safePageable)
                : authorRepository.findByNameContainingIgnoreCase(find.trim(), safePageable);
        return PageResponse.of(authors.map(authorMapper::toResponse));
    }

    @Override
    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No author with id: " + id));
        return authorMapper.toResponse(author);
    }

    @Override
    public AuthorResponse createAuthor(AuthorRequest request) {
        String authorName = normalizeName(request.name());
        authorRepository.findByNameIgnoreCase(authorName).ifPresent(author -> {
            throw new DuplicateResourceException("Author already exists with name: " + authorName);
        });

        Author newAuthor = new Author();
        newAuthor.setName(authorName);
        newAuthor.setDescription(request.description());
        Author savedAuthor = authorRepository.save(newAuthor);
        return authorMapper.toResponse(savedAuthor);
    }

    @Override
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No author with id: " + id));
        if (request.name() == null && request.description() == null) {
            throw new IllegalArgumentException("At least one field is required for update");
        }

        if (request.name() != null) {
            String updatedName = normalizeName(request.name());
            authorRepository.findByNameIgnoreCase(updatedName)
                    .filter(author -> !author.getId().equals(id))
                    .ifPresent(author -> {
                        throw new DuplicateResourceException("Author already exists with name: " + updatedName);
                    });
            existingAuthor.setName(updatedName);
        }
        if (request.description() != null) {
            existingAuthor.setDescription(request.description());
        }

        Author updatedAuthor = authorRepository.save(existingAuthor);
        return authorMapper.toResponse(updatedAuthor);
    }

    @Override
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("No author with id: " + id);
        }
        if (authorRepository.existsBooksByAuthorId(id)) {
            throw new DuplicateResourceException("Cannot delete author because it is assigned to books");
        }
        authorRepository.deleteById(id);
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Author name is required");
        }
        return name.trim();
    }

    private Pageable sanitizePageable(Pageable pageable) {
        if (pageable == null) {
            return PageRequest.of(0, 20, Sort.by("name").ascending());
        }

        List<Sort.Order> safeOrders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            String property = normalizeSortProperty(order.getProperty());
            if (ALLOWED_SORT_FIELDS.contains(property)) {
                safeOrders.add(order.withProperty(property));
            }
        }

        Sort sort = safeOrders.isEmpty()
                ? Sort.by("name").ascending()
                : Sort.by(safeOrders);

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    private String normalizeSortProperty(String property) {
        if (property == null) {
            return "";
        }
        return property.replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .replace("'", "")
                .trim();
    }
}

