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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No book with id: " + id));

        return bookMapper.toResponse(book);
    }

    @Override
    @CacheEvict(value = "books_page", allEntries = true)
    @Transactional
    public BookResponse createBook(BookRequest bookRequest) {
        Book newBook = bookMapper.toEntity(bookRequest);

        validateAuthors(newBook, bookRequest);
        validateCategories(newBook, bookRequest);

        Book savedBook = bookRepository.save(newBook);

        return bookMapper.toResponse(savedBook);
    }

    @Override
    @CacheEvict(value = {"books_page", "books"}, allEntries = true)
    @Transactional
    public BookResponse updateBook(Long id, BookRequest bookRequest){
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No book with id: " + id));

        bookMapper.updateBookFromRequest(bookRequest, existingBook);

        validateAuthors(existingBook, bookRequest);
        validateCategories(existingBook, bookRequest);

        Book updatedBook = bookRepository.save(existingBook);

        return bookMapper.toResponse(updatedBook);
    }

    @Override
    @CacheEvict(value = {"books_page", "books"}, allEntries = true)
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("No book with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> searchBooks(SearchBookRequest request, Pageable pageable) {
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

    private void validateAuthors(Book book, BookRequest bookRequest){
        Set<Author> authors = new java.util.HashSet<>(authorRepository.findAllById(bookRequest.authorsIds()));
        if (authors.size() != bookRequest.authorsIds().size()) {
            Set<Long> foundIds = authors.stream()
                    .map(Author::getId)
                    .collect(Collectors.toSet());
            List<Long> missingIds = new ArrayList<>(bookRequest.authorsIds());
            missingIds.removeAll(foundIds);

            throw new ResourceNotFoundException("Can not find author's ID: " + missingIds);
        }
        book.setAuthors(authors);
    }

    private void validateCategories(Book book, BookRequest bookRequest){
        Set<Category> categories = new java.util.HashSet<>(categoryRepository.findAllById(bookRequest.categoryIds()));
        if (categories.size() != bookRequest.categoryIds().size()) {
            Set<Long> foundIds = categories.stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());
            List<Long> missingIds = new ArrayList<>(bookRequest.categoryIds());
            missingIds.removeAll(foundIds);

            throw new ResourceNotFoundException("Can not find category's ID: " + missingIds);
        }
        book.setCategories(categories);
    }
}
