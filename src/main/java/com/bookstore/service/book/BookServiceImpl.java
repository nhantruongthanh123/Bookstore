package com.bookstore.service.book;

import com.bookstore.dto.Book.BookRequest;
import com.bookstore.dto.Book.BookResponse;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.mapper.BookMapper;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CategoryRepository categoryRepository;


    @Override
    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(bookMapper::toDto).toList();
    }

    @Override
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No book with id: " + id));

        return bookMapper.toDto(book);
    }

    @Override
    public BookResponse createBook(BookRequest bookRequest) {
        Book newBook = bookMapper.toEntity(bookRequest);

        Set<Category> categories = new java.util.HashSet<>(categoryRepository.findAllById(bookRequest.categoryIds()));
        newBook.setCategories(categories);

        Book savedBook = bookRepository.save(newBook);

        return bookMapper.toDto(savedBook);
    }

    @Override
    public BookResponse updateBook(Long id, BookRequest bookRequest){
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No book with id: " + id));

        bookMapper.updateBookFromRequest(bookRequest, existingBook);
        Set<Category> categories = new java.util.HashSet<>(categoryRepository.findAllById(bookRequest.categoryIds()));
        existingBook.setCategories(categories);

        Book updatedBook = bookRepository.save(existingBook);

        return bookMapper.toDto(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("No book with id: " + id);
        }
        bookRepository.deleteById(id);
    }
}
