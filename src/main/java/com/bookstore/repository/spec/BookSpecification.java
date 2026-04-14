package com.bookstore.repository.spec;

import com.bookstore.entity.Author;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class BookSpecification {
    public static Specification<Book> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> hasAuthor(Author author) {
        return (root, query, cb) -> {
            if (author == null) return null;
            query.distinct(true);
            Join<Book, Author> authorJoin = root.join("authors");
            if (author.getId() != null) {
                return cb.equal(authorJoin.get("id"), author.getId());
            }
            if (author.getName() == null || author.getName().isBlank()) {
                return null;
            }
            return cb.like(cb.lower(authorJoin.get("name")),
                    "%" + author.getName().trim().toLowerCase() + "%");
        };
    }

    public static Specification<Book> hasCategory(String categoryName) {
        return (root, query, cb) -> {
            if (categoryName == null) return null;
            query.distinct(true);
            Join<Book, Category> categories = root.join("categories");
            return cb.equal(cb.lower(categories.get("name")),
                    categoryName.toLowerCase());
        };
    }

    public static Specification<Book> priceBetween(BigDecimal minPrice,
                                                   BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return null;
            if (minPrice == null)
                return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            if (maxPrice == null)
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            return cb.between(root.get("price"), minPrice, maxPrice);
        };
    }

    public static Specification<Book> isNotDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
    }
}
