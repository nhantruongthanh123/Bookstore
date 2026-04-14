package com.bookstore.repository;

import com.bookstore.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByNameIgnoreCase(String name);
    Page<Author> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("""
            select count(b.id) > 0
            from Author a
            join a.books b
            where a.id = :authorId
            """)
    boolean existsBooksByAuthorId(@Param("authorId") Long authorId);
}

