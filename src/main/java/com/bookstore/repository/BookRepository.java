package com.bookstore.repository;

import com.bookstore.entity.Book;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @EntityGraph(attributePaths = {"categories", "authors"})
    @NonNull
    @Override
    List<Book> findAll();

    @EntityGraph(attributePaths = {"categories", "authors"})
    @NonNull
    @Override
    Page<Book> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"categories", "authors"})
    @NonNull
    @Override
    Optional<Book> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"categories", "authors"})
    List<Book> findByIdIn(List<Long> ids);

    @Query("""
            select b
            from Book b
            where b.isDeleted = false
            order by b.quantity asc, b.id asc
            """)
    List<Book> findBooksWithLowestQuantity(Pageable pageable);

    @Query("""
            select c, count(b.id)
            from Book b
            join b.categories c
            where b.isDeleted = false
            group by c
            order by count(b.id) desc
            """)
    List<Object[]> findTopCategoriesByBookCount(Pageable pageable);

    @Query("""
            select count(b.id)
            from Book b
            where b.isDeleted = false
            """)
    long countAvailableBooks();
}
