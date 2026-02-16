package com.example.demo.controller;

import com.example.demo.DTO.BookDTO;
import com.example.demo.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }


    @PostMapping("/book/create")
    public ResponseEntity<Integer> createBook(@Valid @RequestBody BookDTO bookDTO) {
        Integer newBookId = bookService.createBook(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBookId);
    }


}
