package com.example.demo.service;


import com.example.demo.DTO.BookDTO;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;

import javax.swing.text.html.Option;
import java.util.*;

@Slf4j
@Service
public class BookService {

    private Logger logger = LoggerFactory.getLogger(BookService.class);
    private RestClient hardcoverApiClient;
    private BookRepository bookRepository;

    public BookService(@Value("${hardcover.api.key}") String hardcoverApiKey, BookRepository bookRepository) {
        this.hardcoverApiClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("https://api.hardcover.app/v1/graphql")
                .defaultHeader("authorization", hardcoverApiKey)
                .build();

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

    public Integer createBook(BookDTO bookDTO) {
        Book book;
        try {
            JsonNode response = getApiResponse(bookDTO.title());
            book = parseBookDetails(response, bookDTO);
            bookRepository.save(book);
        } catch (NullPointerException | JsonNodeException e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return book.getId();
    }

    private Book parseBookDetails(JsonNode hardcoverResponse, BookDTO bookDTO) throws JsonNodeException {

        Book book = Book.builder()
                .title(bookDTO.title())
                .author(bookDTO.author())
                .totalChapters(bookDTO.totalChapters())
                .rating(hardcoverResponse.path("rating").asFloat())
                .reviewCount(hardcoverResponse.path("ratings_count").asInt())
                .coverUrl(hardcoverResponse.path("image").path("url").asString())
                .summary(hardcoverResponse.path("description").asString())
                .build();

        return book;
    }

    private JsonNode getApiResponse(String bookTitle) {
        String query = """
        query GetEditionsFromTitle($bookName: String!) {
            books(
                where: {title: {_eq: $bookName}}
                order_by: {ratings_count: desc}
                limit: 1
            ) {
                id
                title
                description
                pages
                release_date
                ratings_count
                rating
                cached_tags
                image {
                    url
                }
                user_books(where: {has_review: {_eq: true}}) {
                    review_raw
                    rating
                    reviewed_at
                    user {
                        username
                        image {
                            url
                        }
                    }
                }
            }
        }
        """;

        Map<String, Object> variables = Map.of(
                "bookName", bookTitle
        );

        Map<String, Object> body = Map.of(
                "query", query,
                "variables", variables
        );

        JsonNode response = hardcoverApiClient.post()
                .body(body)
                .retrieve()
                .body(JsonNode.class);


        return response.path("data").path("books").get(0);
    }
 }
