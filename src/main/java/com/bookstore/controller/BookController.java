package com.bookstore.controller;

import com.bookstore.repository.BookRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class BookController {
    BookRepository bookReposiory;

    @GetMapping("/test")
    public String getTest(){
        return "test";
    }

}
