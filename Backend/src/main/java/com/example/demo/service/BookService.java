package com.example.demo.service;


import com.example.demo.DTO.CreateBookDTO;
import com.example.demo.model.Book;
import com.example.demo.model.Genre;
import com.example.demo.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import java.util.*;

@Slf4j
@Service
public class BookService {

    private Logger logger = LoggerFactory.getLogger(BookService.class);
    private HardcoverService hardcoverService;
    private GenreService genreService;
    private BookRepository bookRepository;


    public BookService(BookRepository bookRepository, HardcoverService hardcoverService, GenreService genreService) {
        this.hardcoverService = hardcoverService;
        this.genreService = genreService;
        this.bookRepository = bookRepository;

    }

    public Optional<Book> getBookById(int bookId) {
        return bookRepository.findById(bookId);
    }

    public List<Book> getPopularBooks(int maxResults) {
        return bookRepository.findNMostPopularBooks(maxResults);
    }

    public List<Book> searchBooks(String bookTitle) {
        return bookRepository.searchBooksByTitle(bookTitle);
    }

    public Integer createBook(CreateBookDTO createBookDTO) {
        Book book;
        try {
            JsonNode response = hardcoverService.getApiResponse(createBookDTO.title());
            book = hardcoverService.createAndMapBook(createBookDTO, response);
            Set<Genre> genres = genreService.resolveGenres(hardcoverService.parseGenreNames(response));
            book.setGenres(genres);
            bookRepository.save(book);
        } catch (NullPointerException | JsonNodeException e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return book.getId();
    }






 }
