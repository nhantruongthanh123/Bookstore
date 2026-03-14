package com.bookstore.service.book;

import com.bookstore.entity.Book;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Override
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Integer id, Book bookDetails){
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No book with id: " + id));

        existingBook.setAuthor(bookDetails.getAuthor());
        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setPublisher(bookDetails.getPublisher());
        existingBook.setPrice(bookDetails.getPrice());

        return bookRepository.save(existingBook);
    }

    @Override
    public void deleteBook(Integer id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("No book with id: " + id);
        }

        bookRepository.deleteById(id);
    }


}
