package com.example.demo.controller;

import com.example.demo.DTO.BookDTO;
import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/book/getPopular")
    public ResponseEntity<List<Book>> getPopularBooks(@RequestParam(required = false, defaultValue = "10") Integer maxResults) {
        List<Book> results = bookService.getPopularBooks(maxResults);
        return ResponseEntity.ok().body(results);
    }

    @GetMapping("/book/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String bookTitle) {
        List<Book> results = bookService.searchBooks(bookTitle);
        return ResponseEntity.ok().body(results);
    }



}
