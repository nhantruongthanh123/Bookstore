package com.bookstore.controller;

import com.bookstore.entity.Book;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class BookController {
    @Autowired
    private final BookRepository bookRepository;

    @GetMapping("/all")
    public List<Book> all(){
        return bookRepository.findAll();
    }

    @GetMapping("book/{id}")
    public Book findById(@PathVariable Integer id){
        return bookRepository.findById(id).get();
    }

    @PostMapping("/add")
    public Book create(@RequestBody Book book){
        return bookRepository.save(book);
    }

    @PutMapping("update/{id}")
    public Book update(@RequestBody Book book, @PathVariable Integer id){
        return  bookRepository.save(book);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Integer id){

        bookRepository.deleteById(id);
    }

}
