package com.example.demo.service;


import com.example.demo.DTO.BookDTO;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;

import java.util.*;

@Slf4j
@Service
public class BookService {

    RestClient googleBookApiClient;
    BookRepository bookRepository;

    @Value("${google.books.api.key}")
    private String googleBooksAPIKey;

    public BookService(@Value("${google.books.api.key}") String googleBooksAPIKey, BookRepository bookRepository) {
        this.googleBookApiClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("https://www.googleapis.com/books/v1/volumes")
                .build();

        this.bookRepository = bookRepository;
    }

    public List<Book> getPopularBooks(int maxResults) {
        return bookRepository.findNMostPopularBooks(maxResults);
    }

    public List<Book> searchBooks(String bookTitle) {
        return bookRepository.searchBooksByTitle(bookTitle);
    }


    public Integer createBook(BookDTO bookDTO) {
        JsonNode bookJson = getBookDetails(bookDTO);

        Book book = Book.builder()
                .title(bookDTO.title())
                .author(bookDTO.author())
                .totalChapters(bookDTO.totalChapters())
                .summary(bookJson.path("volumeInfo").path("description").asString(""))
                .coverUrl(bookJson.path("volumeInfo").path("imageLinks").path("thumbnail").asString())
                .build();

        bookRepository.save(book);
        return book.getId();
    }

    private JsonNode getBookDetails(BookDTO bookDTO) {
        JsonNode response = googleBookRequest(bookDTO.title(), bookDTO.author());

        if (response != null && response.has("items")) {
            List<JsonNode> itemList = new ArrayList<>();
            response.get("items").forEach(itemList::add);

            Optional<JsonNode> bestBook = itemList.stream()
                    .filter(item -> item.path("volumeInfo").path("title").asText()
                            .equalsIgnoreCase(bookDTO.title()))
                    .filter(item -> item.path("volumeInfo").path("imageLinks").has("thumbnail"))
                    .findFirst();

            if (bestBook.isEmpty()) {
                bestBook = itemList.stream()
                        .max(Comparator.comparingInt(item ->
                                item.path("volumeInfo").path("pageCount").asInt(0)));
            }

            bestBook.ifPresent(jsonNode ->
                    LoggerFactory.getLogger(BookService.class).info("Best Match: \n{}", jsonNode.path("volumeInfo").path("imageLinks").path("thumbnail").asString())
            );

            if(bestBook.isPresent()) {
                return bestBook.get();
            }
        }

        throw new ResponseStatusException(HttpStatusCode.valueOf(500));
    }

    private JsonNode googleBookRequest(String bookTitle, String bookAuthor) {
        String query = String.format("intitle:\"%s\"+inauthor:\"%s\"", bookTitle, bookAuthor);

        JsonNode response = googleBookApiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", googleBooksAPIKey)
                        .queryParam("q", query)
                        .queryParam("printType", "books")
                        .queryParam("maxResults", 3)
                        .build())
                .retrieve()
                .body(JsonNode.class);

        return response;
    }
 }
