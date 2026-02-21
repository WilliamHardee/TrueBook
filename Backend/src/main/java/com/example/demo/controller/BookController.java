package com.example.demo.controller;

import com.example.demo.DTO.CreateBookDTO;
import com.example.demo.DTO.PopularBookDTO;
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
    public ResponseEntity<Integer> createBook(@Valid @RequestBody CreateBookDTO createBookDTO) {
        Integer newBookId = bookService.createBook(createBookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBookId);
    }

    @GetMapping("/book/getPopular")
    public ResponseEntity<List<PopularBookDTO>> getPopularBooks(@RequestParam(required = false, defaultValue = "10") Integer maxResults) {
        List<Book> results = bookService.getPopularBooks(maxResults);

        List<PopularBookDTO> response = results.stream()
                .map(book -> new PopularBookDTO(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCoverUrl()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/book/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String bookTitle) {
        List<Book> results = bookService.searchBooks(bookTitle);
        return ResponseEntity.ok().body(results);
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<Book> getById(@PathVariable int id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



}
