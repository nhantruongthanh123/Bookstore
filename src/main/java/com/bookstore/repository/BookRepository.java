package com.bookstore.repository;

import com.bookstore.entity.Book;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph(attributePaths = {"categories"})
    @NonNull
    @Override
    List<Book> findAll();
}
