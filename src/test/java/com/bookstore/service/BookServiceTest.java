package com.bookstore.service;

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
import com.bookstore.service.book.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void getBookById_ShouldReturnBookResponse_WhenBookExists() {
        Book book = createBook(1L);
        BookResponse expectedResponse = createBookResponse(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toResponse(book)).thenReturn(expectedResponse);

        BookResponse response = bookService.getBookById(1L);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void getBookById_ShouldThrowResourceNotFoundException_WhenBookDoesNotExist() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No book with id: 999");
    }

    @Test
    void createBook_ShouldCreateBook_WhenAuthorsAndCategoriesExist() {
        BookRequest request = createBookRequest(Set.of(1L, 2L), Set.of(10L));
        Book newBook = createBook(null);
        Book savedBook = createBook(100L);
        BookResponse expectedResponse = createBookResponse(100L);

        when(bookMapper.toEntity(request)).thenReturn(newBook);
        when(authorRepository.findAllById(request.authorsIds())).thenReturn(List.of(createAuthor(1L), createAuthor(2L)));
        when(categoryRepository.findAllById(request.categoryIds())).thenReturn(List.of(createCategory(10L)));
        when(bookRepository.save(newBook)).thenReturn(savedBook);
        when(bookMapper.toResponse(savedBook)).thenReturn(expectedResponse);

        BookResponse response = bookService.createBook(request);

        assertThat(response).isEqualTo(expectedResponse);
        assertThat(newBook.getAuthors()).extracting(Author::getId).containsExactlyInAnyOrder(1L, 2L);
        assertThat(newBook.getCategories()).extracting(Category::getId).containsExactly(10L);
    }

    @Test
    void createBook_ShouldThrowResourceNotFoundException_WhenAnyAuthorDoesNotExist() {
        BookRequest request = createBookRequest(Set.of(1L, 2L), Set.of(10L));
        Book newBook = createBook(null);

        when(bookMapper.toEntity(request)).thenReturn(newBook);
        when(authorRepository.findAllById(request.authorsIds())).thenReturn(List.of(createAuthor(1L)));

        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Can not find author's ID: [2]");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void createBook_ShouldThrowResourceNotFoundException_WhenAnyCategoryDoesNotExist() {
        BookRequest request = createBookRequest(Set.of(1L), Set.of(10L, 11L));
        Book newBook = createBook(null);

        when(bookMapper.toEntity(request)).thenReturn(newBook);
        when(authorRepository.findAllById(request.authorsIds())).thenReturn(List.of(createAuthor(1L)));
        when(categoryRepository.findAllById(request.categoryIds())).thenReturn(List.of(createCategory(10L)));

        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Can not find category's ID: [11]");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_ShouldUpdateBook_WhenBookAuthorsAndCategoriesExist() {
        Long bookId = 5L;
        BookRequest request = createBookRequest(Set.of(1L), Set.of(10L));
        Book existingBook = createBook(bookId);
        BookResponse expectedResponse = createBookResponse(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findAllById(request.authorsIds())).thenReturn(List.of(createAuthor(1L)));
        when(categoryRepository.findAllById(request.categoryIds())).thenReturn(List.of(createCategory(10L)));
        when(bookRepository.save(existingBook)).thenReturn(existingBook);
        when(bookMapper.toResponse(existingBook)).thenReturn(expectedResponse);

        BookResponse response = bookService.updateBook(bookId, request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(bookMapper).updateBookFromRequest(request, existingBook);
    }

    @Test
    void updateBook_ShouldThrowResourceNotFoundException_WhenBookDoesNotExist() {
        BookRequest request = createBookRequest(Set.of(1L), Set.of(10L));
        when(bookRepository.findById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(50L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No book with id: 50");
    }

    @Test
    void deleteBook_ShouldDeleteBook_WhenBookExists() {
        when(bookRepository.existsById(3L)).thenReturn(true);

        bookService.deleteBook(3L);

        verify(bookRepository).deleteById(3L);
    }

    @Test
    void deleteBook_ShouldThrowResourceNotFoundException_WhenBookDoesNotExist() {
        when(bookRepository.existsById(3L)).thenReturn(false);

        assertThatThrownBy(() -> bookService.deleteBook(3L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No book with id: 3");

        verify(bookRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void getAllBooks_ShouldReturnPageResponse_WhenBooksExist() {
        Pageable pageable = PageRequest.of(0, 2);
        Book firstBook = createBook(1L);
        Book secondBook = createBook(2L);
        BookResponse firstResponse = createBookResponse(1L);
        BookResponse secondResponse = createBookResponse(2L);
        Page<Book> bookPage = new PageImpl<>(List.of(firstBook, secondBook), pageable, 2);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toResponse(firstBook)).thenReturn(firstResponse);
        when(bookMapper.toResponse(secondBook)).thenReturn(secondResponse);

        PageResponse<BookResponse> response = bookService.getAllBooks(pageable);

        assertThat(response.content()).containsExactly(firstResponse, secondResponse);
        assertThat(response.pageNo()).isEqualTo(0);
        assertThat(response.pageSize()).isEqualTo(2);
        assertThat(response.totalElements()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(1);
        assertThat(response.last()).isTrue();
    }

    @Test
    void searchBooks_ShouldReturnPageResponse_WhenFilterRequestProvided() {
        SearchBookRequest request = new SearchBookRequest("Dune", null, "Sci-fi", new BigDecimal("10.00"), new BigDecimal("100.00"));
        Pageable pageable = PageRequest.of(0, 1);
        Book book = createBook(11L);
        BookResponse bookResponse = createBookResponse(11L);
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(bookPage);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        PageResponse<BookResponse> response = bookService.searchBooks(request, pageable);

        assertThat(response.content()).containsExactly(bookResponse);
        assertThat(response.totalElements()).isEqualTo(1);
        verify(bookRepository).findAll(any(Specification.class), eq(pageable));
    }

    private Book createBook(Long id) {
        Book book = new Book();
        book.setId(id);
        book.setTitle("Test Book");
        book.setPrice(new BigDecimal("59.99"));
        book.setQuantity(10);
        return book;
    }

    private Author createAuthor(Long id) {
        Author author = new Author();
        author.setId(id);
        author.setName("Author " + id);
        return author;
    }

    private Category createCategory(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName("Category " + id);
        category.setDescription("Description " + id);
        return category;
    }

    private BookRequest createBookRequest(Set<Long> authorIds, Set<Long> categoryIds) {
        return new BookRequest(
                "Dune",
                authorIds,
                "Ace",
                new BigDecimal("50.00"),
                "ISBN-1234",
                "Test description",
                "cover.jpg",
                20,
                categoryIds
        );
    }

    private BookResponse createBookResponse(Long id) {
        return new BookResponse(
                id,
                "Dune",
                Set.of(),
                "Ace",
                new BigDecimal("50.00"),
                "ISBN-1234",
                "Test description",
                "cover.jpg",
                20,
                Set.of()
        );
    }
}
