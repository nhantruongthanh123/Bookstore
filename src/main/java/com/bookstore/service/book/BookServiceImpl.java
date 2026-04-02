package com.bookstore.service.book;

import com.bookstore.dto.Book.BookRequest;
import com.bookstore.dto.Book.BookResponse;
import com.bookstore.dto.Book.SearchBookRequest;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.repository.spec.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CategoryRepository categoryRepository;


    @Override
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(bookMapper::toResponse);
    }

    @Override
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No book with id: " + id));

        return bookMapper.toResponse(book);
    }

    @Override
    public BookResponse createBook(BookRequest bookRequest) {
        Book newBook = bookMapper.toEntity(bookRequest);

        Set<Category> categories = new java.util.HashSet<>(categoryRepository.findAllById(bookRequest.categoryIds()));
        newBook.setCategories(categories);

        Book savedBook = bookRepository.save(newBook);

        return bookMapper.toResponse(savedBook);
    }

    @Override
    public BookResponse updateBook(Long id, BookRequest bookRequest){
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No book with id: " + id));

        bookMapper.updateBookFromRequest(bookRequest, existingBook);
        Set<Category> categories = new java.util.HashSet<>(categoryRepository.findAllById(bookRequest.categoryIds()));
        existingBook.setCategories(categories);

        Book updatedBook = bookRepository.save(existingBook);

        return bookMapper.toResponse(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("No book with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    public Page<BookResponse> searchBooks(SearchBookRequest request, Pageable pageable){
        Specification<Book> spec = Specification.where(BookSpecification.isNotDeleted())
                .and(BookSpecification.hasTitle(request.title()))
                .and(BookSpecification.hasAuthor(request.author()))
                .and(BookSpecification.hasCategory(request.category()))
                .and(BookSpecification.priceBetween(request.minPrice(),
                        request.maxPrice()));

        Page<Book> books = bookRepository.findAll(spec, pageable);
        return books.map(bookMapper::toResponse);
    }
}
