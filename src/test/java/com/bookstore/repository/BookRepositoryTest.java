package com.bookstore.repository;

import com.bookstore.entity.Author;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAll_ShouldReturnBooksAndFetchCategories() {
        Category category = new Category();
        category.setName("Sci-fi");
        category.setDescription("A genre of speculative fiction that explores the potential consequences of imagined innovations in science and technology");
        Category savedCategory = entityManager.persist(category);

        Author author = new Author();
        author.setName("Frank Herbert");
        Author savedAuthor = entityManager.persist(author);

        Book book = new Book();
        book.setTitle("Dune");
        book.setAuthors(new HashSet<>(Set.of(savedAuthor)));
        book.setQuantity(100);
        book.setPrice(new BigDecimal("50.0"));
        book.setCategories(new HashSet<>(Set.of(savedCategory)));

        entityManager.persist(book);
        entityManager.flush();
        entityManager.clear();

        List<Book> books = bookRepository.findAll();

        assertThat(books).isNotEmpty();

        Book retrievedBook = books.stream()
                .filter(b -> "Dune".equals(b.getTitle()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Don't find Dune in the database!"));

        assertThat(retrievedBook.getCategories()).isNotEmpty();
        assertThat(retrievedBook.getCategories().iterator().next().getName()).isEqualTo("Sci-fi");
        assertThat(retrievedBook.getAuthors()).isNotEmpty();
        assertThat(retrievedBook.getAuthors().iterator().next().getName()).isEqualTo("Frank Herbert");
    }
}
