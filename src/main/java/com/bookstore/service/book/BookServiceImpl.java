package com.bookstore.service.book;

import com.bookstore.dto.Book.BookRequest;
import com.bookstore.dto.Book.BookResponse;
import com.bookstore.dto.Book.SearchBookRequest;
import com.bookstore.dto.Page.PageResponse;
import com.bookstore.entity.Author;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.repository.AuthorRepository;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.repository.spec.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;


    @Override
    @Cacheable(value = "books_page", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getAllBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        Page<BookResponse> responsePage = books.map(bookMapper::toResponse);
        return PageResponse.of(responsePage);
    }

    @Cacheable(value = "books", key = "#id")
    @Override
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No book with id: " + id));

        return bookMapper.toResponse(book);
    }

    @Override
    @CacheEvict(value = "books_page", allEntries = true)
    public BookResponse createBook(BookRequest bookRequest) {
        Book newBook = bookMapper.toEntity(bookRequest);
        newBook.setAuthors(resolveAuthors(bookRequest));

        Set<Category> categories = new java.util.HashSet<>(categoryRepository.findAllById(bookRequest.categoryIds()));
        newBook.setCategories(categories);

        Book savedBook = bookRepository.save(newBook);

        return bookMapper.toResponse(savedBook);
    }

    @Override
    @CacheEvict(value = {"books_page", "books"}, allEntries = true)
    public BookResponse updateBook(Long id, BookRequest bookRequest){
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No book with id: " + id));

        bookMapper.updateBookFromRequest(bookRequest, existingBook);
        existingBook.setAuthors(resolveAuthors(bookRequest));
        Set<Category> categories = new java.util.HashSet<>(categoryRepository.findAllById(bookRequest.categoryIds()));
        existingBook.setCategories(categories);

        Book updatedBook = bookRepository.save(existingBook);

        return bookMapper.toResponse(updatedBook);
    }

    @Override
    @CacheEvict(value = {"books_page", "books"}, allEntries = true)
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("No book with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> searchBooks(SearchBookRequest request, Pageable pageable){
        Specification<Book> spec = Specification.where(BookSpecification.isNotDeleted())
                .and(BookSpecification.hasTitle(request.title()))
                .and(BookSpecification.hasAuthor(request.author()))
                .and(BookSpecification.hasCategory(request.category()))
                .and(BookSpecification.priceBetween(request.minPrice(),
                        request.maxPrice()));

        Page<Book> books = bookRepository.findAll(spec, pageable);
        Page<BookResponse> responsePage = books.map(bookMapper::toResponse);
        return PageResponse.of(responsePage);
    }

    private Set<Author> resolveAuthors(BookRequest request) {
        Set<Author> resolved = new LinkedHashSet<>();
        for (Long authorId : request.authorsIds()) {
            if (authorId == null) {
                continue;
            }
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("No author with id: " + authorId));
            resolved.add(author);
        }

        if (resolved.isEmpty()) {
            throw new IllegalArgumentException("Author is required");
        }

        return resolved;
    }
}
